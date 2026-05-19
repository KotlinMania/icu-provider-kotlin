// port-lint: source marker.rs
package io.github.kotlinmania.icuprovider

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

    companion object {
        /** Magic bytes to locate [DataMarkerIdHash]es in binaries. */
        val LEADING_TAG: ByteArray = byteArrayOf('t'.code.toByte(), 'd'.code.toByte(), 'm'.code.toByte(), 'h'.code.toByte())

        fun fromBytes(bytes: ByteArray): DataMarkerIdHash = DataMarkerIdHash(bytes.copyOf())
    }
}

/** Error returned when a data marker name is not shaped like a versioned marker identifier. */
data class DataMarkerIdNameError(
    val expected: String,
    val index: Int,
) : Exception("expected $expected at $index")

/** The ID of a data marker. */
class DataMarkerId private constructor(
    val debug: String,
    private val hash: ByteArray,
) : Comparable<DataMarkerId> {
    init {
        require(hash.size == 8)
    }

    /** Gets a platform-independent hash of a [DataMarkerId]. */
    fun hashed(): DataMarkerIdHash = DataMarkerIdHash.fromBytes(hash.copyOfRange(4, 8))

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
}

/** Const-compatible FxHash logic from ICU4X, expressed as a regular common Kotlin function. */
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
