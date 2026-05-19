// port-lint: source marker.rs
package io.github.kotlinmania.icuprovider

/**
 * Trait marker for data structs. All types delivered by the data provider must be associated with
 * something implementing this interface.
 *
 * Data markers are normally generated with the data marker helper.
 *
 * Also see [DataMarker].
 *
 * Note: dynamic data markers are quasi-const-generic compile-time objects, and as such are expected
 * to be unit objects. Since this is not something the type system can enforce, Kotlin represents
 * this as a regular marker object contract.
 */
interface DynamicDataMarker<DataStruct : Any>

/**
 * A [DynamicDataMarker] with a [DataMarkerInfo] attached.
 *
 * Implementing this interface enables this marker to be used with the main [DataProvider] interface.
 * Most markers should be associated with a specific marker and should therefore implement this
 * interface.
 */
interface DataMarker<DataStruct : Any> : DynamicDataMarker<DataStruct> {
    /** The single [DataMarkerInfo] associated with this marker. */
    val info: DataMarkerInfo
}

/** Binds a [DataMarker] to a provider supporting it. */
fun <M, DataStruct : Any, P> M.bind(provider: P): DataProviderWithMarker<M, DataStruct, P>
    where M : DataMarker<DataStruct>,
          P : DataProvider<M, DataStruct> =
    DataProviderWithMarker(provider, this)

/** Constructs a [DataLocale] using fallback preferences from this [DataMarker]. */
fun DataMarker<*>.makeLocale(locale: LocalePreferences): DataLocale = info.makeLocale(locale)

/**
 * A [DynamicDataMarker] that never returns data.
 *
 * Provider implementations for concrete markers are expected to handle [NeverMarker] by returning
 * [DataErrorKind.MarkerNotFound].
 */
class NeverMarker<Y : Any> : DataMarker<Y> {
    override val info: DataMarkerInfo = INFO

    companion object {
        val INFO: DataMarkerInfo = DataMarkerInfo.fromId(
            DataMarkerId.fromName("NeverMarkerV1").getOrThrow(),
        )
    }
}

/** Loads [NeverMarker] by returning [DataErrorKind.MarkerNotFound]. */
fun <Y : Any> dataProviderNeverMarkerLoad(request: DataRequest): Result<DataResponse<NeverMarker<Y>, Y>> =
    Result.failure(DataErrorKind.MarkerNotFound.withReq(NeverMarker.INFO, request))

/**
 * A compact hash of a [DataMarkerInfo]. Useful for keys in maps.
 *
 * The hash will be stable over time within major releases.
 */
class DataMarkerIdHash private constructor(
    private val bytes: ByteArray,
) : Comparable<DataMarkerIdHash> {
    init {
        require(bytes.size == 4)
    }

    /** Gets the hash value as a byte array. */
    fun toBytes(): ByteArray = bytes.copyOf()

    fun toUnaligned(): DataMarkerIdHash = this

    companion object {
        /** Magic bytes to locate [DataMarkerIdHash]es in binaries. */
        val LEADING_TAG: ByteArray =
            byteArrayOf('t'.code.toByte(), 'd'.code.toByte(), 'm'.code.toByte(), 'h'.code.toByte())

        fun fromBytes(bytes: ByteArray): DataMarkerIdHash = DataMarkerIdHash(bytes.copyOf())

        fun fromUnaligned(unaligned: DataMarkerIdHash): DataMarkerIdHash = unaligned
    }

    override fun compareTo(other: DataMarkerIdHash): Int {
        for (index in bytes.indices) {
            val left = bytes[index].toInt() and 0xFF
            val right = other.bytes[index].toInt() and 0xFF
            if (left != right) {
                return left.compareTo(right)
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean =
        other is DataMarkerIdHash && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = bytes.contentHashCode()

    override fun toString(): String = bytes.toUnsignedInts().toString()
}

/**
 * Const-compatible FxHash logic from ICU4X, expressed as a regular common Kotlin function.
 *
 * FxHash is a speedy hash algorithm used within rustc. The algorithm is satisfactory for this use
 * case since the strings being hashed originate from a trusted source, and the hashes are computed
 * by generated marker code, so collisions can be checked.
 */
internal fun fxhash32(bytes: ByteArray): Long {
    fun hashWord32(hash: Long, word: Long): Long {
        val rotated = ((hash shl 5) or (hash ushr 27)) and UINT_MASK
        return ((rotated xor (word and UINT_MASK)) * SEED32) and UINT_MASK
    }

    var cursor = 0
    val end = bytes.size
    var hash = 0L

    while (end - cursor >= 4) {
        val word =
            (bytes[cursor].toLong() and BYTE_MASK) or
                ((bytes[cursor + 1].toLong() and BYTE_MASK) shl 8) or
                ((bytes[cursor + 2].toLong() and BYTE_MASK) shl 16) or
                ((bytes[cursor + 3].toLong() and BYTE_MASK) shl 24)
        hash = hashWord32(hash, word)
        cursor += 4
    }

    if (end - cursor >= 2) {
        val word =
            (bytes[cursor].toLong() and BYTE_MASK) or
                ((bytes[cursor + 1].toLong() and BYTE_MASK) shl 8)
        hash = hashWord32(hash, word)
        cursor += 2
    }

    if (end - cursor >= 1) {
        hash = hashWord32(hash, bytes[cursor].toLong() and BYTE_MASK)
    }

    return hash
}

/** Error returned when a data marker name is not shaped like a versioned marker identifier. */
data class DataMarkerIdNameError(
    val expected: String,
    val index: Int,
) : Exception("expected $expected at $index")

/**
 * The ID of a data marker.
 *
 * This is generally a [DataMarkerIdHash]. In Kotlin, this also contains a human-readable string for
 * an improved debug representation.
 */
class DataMarkerId private constructor(
    val debug: String,
    private val hash: ByteArray,
) : Comparable<DataMarkerId> {
    init {
        require(hash.size == 8)
    }

    /** Gets a platform-independent hash of a [DataMarkerId]. */
    fun hashed(): DataMarkerIdHash = DataMarkerIdHash.fromBytes(hash.copyOfRange(4, 8))

    /** Returns the marker name. */
    fun name(): String = debug

    companion object {
        fun fromName(name: String): Result<DataMarkerId> {
            if (name.isEmpty() || !name.last().isDigit()) {
                return Result.failure(DataMarkerIdNameError("[0-9]", name.length))
            }

            var index = name.length - 1
            while (index > 0 && name[index - 1].isDigit()) {
                index -= 1
            }
            if (index == 0 || name[index - 1] != 'V') {
                return Result.failure(DataMarkerIdNameError("V", index))
            }

            val markerHash = fxhash32(name.encodeToByteArray()).toLittleEndianBytes()
            return Result.success(
                DataMarkerId(
                    debug = name,
                    hash = byteArrayOf(
                        DataMarkerIdHash.LEADING_TAG[0],
                        DataMarkerIdHash.LEADING_TAG[1],
                        DataMarkerIdHash.LEADING_TAG[2],
                        DataMarkerIdHash.LEADING_TAG[3],
                        markerHash[0],
                        markerHash[1],
                        markerHash[2],
                        markerHash[3],
                    ),
                ),
            )
        }
    }

    override fun compareTo(other: DataMarkerId): Int {
        for (index in hash.indices) {
            val left = hash[index].toInt() and 0xFF
            val right = other.hash[index].toInt() and 0xFF
            if (left != right) {
                return left.compareTo(right)
            }
        }
        return 0
    }

    override fun equals(other: Any?): Boolean =
        other is DataMarkerId && hash.contentEquals(other.hash)

    override fun hashCode(): Int = hash.contentHashCode()

    override fun toString(): String = debug
}

/**
 * Used for loading data from a dynamic ICU4X data provider.
 *
 * A data marker is tightly coupled with the code that uses it to load data at runtime.
 * Executables can be searched for [DataMarkerInfo] instances to produce optimized data files.
 * Therefore, users should not generally create [DataMarkerInfo] instances; they should instead use
 * the ones exported by a component.
 */
data class DataMarkerInfo(
    /** The ID of this marker. */
    val id: DataMarkerId,
    /** Whether this data marker only has a single payload, not keyed by a data identifier. */
    val isSingleton: Boolean = false,
    /** Whether this data marker uses checksums for integrity purposes. */
    val hasChecksum: Boolean = false,
    /** The fallback to use for this data marker. */
    val fallbackConfig: LocaleFallbackConfig = LocaleFallbackConfig.default(),
    /** The attributes domain for this data marker. */
    val attributesDomain: String = "",
    /** Whether to create constants for each data struct in baked data. */
    val exposeBakedConsts: Boolean = false,
) : Comparable<DataMarkerInfo> {
    override fun compareTo(other: DataMarkerInfo): Int = id.compareTo(other.id)

    /** Returns success if this data marker matches the argument, or the appropriate error. */
    fun matchMarker(marker: DataMarkerInfo): Result<Unit> =
        if (this == marker) {
            Result.success(Unit)
        } else {
            Result.failure(DataErrorKind.MarkerNotFound.withMarker(marker))
        }

    /** Constructs a [DataLocale] for this [DataMarkerInfo]. */
    fun makeLocale(locale: LocalePreferences): DataLocale =
        if (fallbackConfig.priority == LocaleFallbackPriority.Region) {
            locale.toDataLocaleRegionPriority()
        } else {
            locale.toDataLocaleLanguagePriority()
        }

    override fun toString(): String = id.debug

    companion object {
        /** See the default constructor. */
        fun fromId(id: DataMarkerId): DataMarkerInfo = DataMarkerInfo(id = id)
    }
}

/** Creates a data marker object. */
fun <DataStruct : Any> dataMarker(
    name: String,
    fallbackConfig: LocaleFallbackConfig = LocaleFallbackConfig.default(),
    isSingleton: Boolean = false,
    hasChecksum: Boolean = false,
    attributesDomain: String = "",
    exposeBakedConsts: Boolean = false,
): DataMarkerInfo {
    val id = DataMarkerId.fromName(name).getOrThrow()
    return DataMarkerInfo(
        id = id,
        fallbackConfig = fallbackConfig,
        isSingleton = isSingleton,
        hasChecksum = hasChecksum,
        attributesDomain = attributesDomain,
        exposeBakedConsts = exposeBakedConsts,
    )
}

/** A marker for the given data struct. */
class ErasedMarker<DataStruct : Any>(
    override val info: DataMarkerInfo,
) : DataMarker<DataStruct>

internal fun ByteArray.toUnsignedInts(): List<Int> =
    map { byte -> byte.toInt() and 0xFF }

private fun Long.toLittleEndianBytes(): ByteArray =
    byteArrayOf(
        (this and BYTE_MASK).toByte(),
        ((this ushr 8) and BYTE_MASK).toByte(),
        ((this ushr 16) and BYTE_MASK).toByte(),
        ((this ushr 24) and BYTE_MASK).toByte(),
    )

private const val BYTE_MASK = 0xFFL
private const val UINT_MASK = 0xFFFF_FFFFL
private const val SEED32 = 0x9E37_79B9L
