// port-lint: source request.rs
package io.github.kotlinmania.icuprovider

/** Locale data identifier re-exported by the provider crate. */
data class DataLocale(
    val value: String = "und",
) : Comparable<DataLocale> {
    fun isUnknown(): Boolean = value == "und" || value.isEmpty()

    fun totalCompare(other: DataLocale): Int = compareTo(other)

    override fun compareTo(other: DataLocale): Int = value.compareTo(other.value)

    override fun toString(): String = value
}

/** Locale fallback preferences used to construct a [DataLocale]. */
data class LocalePreferences(
    val locale: DataLocale = DataLocale(),
) {
    fun toDataLocaleRegionPriority(): DataLocale = locale

    fun toDataLocaleLanguagePriority(): DataLocale = locale
}

/** The request type passed into all data provider implementations. */
data class DataRequest(
    /** The data identifier for which to load data. */
    val id: DataIdentifierBorrowed = DataIdentifierBorrowed(),
    /** Metadata that may affect the behavior of the data provider. */
    val metadata: DataRequestMetadata = DataRequestMetadata(),
)

/**
 * Metadata for data requests. This is currently empty, but it may be extended with options for
 * tuning locale fallback, buffer layout, and so forth.
 */
data class DataRequestMetadata(
    /** Silent requests do not log errors. This can be used for exploratory querying, such as fallbacks. */
    val silent: Boolean = false,
    /** Whether to allow prefix matches for the data marker attributes. */
    val attributesPrefixMatch: Boolean = false,
) : Comparable<DataRequestMetadata> {
    override fun compareTo(other: DataRequestMetadata): Int =
        compareValuesBy(this, other, DataRequestMetadata::silent, DataRequestMetadata::attributesPrefixMatch)
}

/** The borrowed version of a [DataIdentifierCow]. */
data class DataIdentifierBorrowed(
    /** Marker-specific request attributes. */
    val markerAttributes: DataMarkerAttributes = DataMarkerAttributes.empty(),
    /** The CLDR locale. */
    val locale: DataLocale = DataLocale(),
) {
    /** Converts this [DataIdentifierBorrowed] into a [DataIdentifierCow]. */
    fun intoOwned(): DataIdentifierCow =
        DataIdentifierCow(
            markerAttributes = markerAttributes,
            locale = locale,
        )

    /** Borrows this [DataIdentifierBorrowed] as a [DataIdentifierCow]. */
    fun asCow(): DataIdentifierCow =
        DataIdentifierCow(
            markerAttributes = markerAttributes,
            locale = locale,
        )

    override fun toString(): String =
        if (markerAttributes.isEmpty()) {
            locale.toString()
        } else {
            "$locale/${markerAttributes.asString()}"
        }

    companion object {
        /** Creates a [DataIdentifierBorrowed] for a borrowed [DataLocale]. */
        fun forLocale(locale: DataLocale): DataIdentifierBorrowed =
            DataIdentifierBorrowed(locale = locale)

        /** Creates a [DataIdentifierBorrowed] for a borrowed [DataMarkerAttributes]. */
        fun forMarkerAttributes(markerAttributes: DataMarkerAttributes): DataIdentifierBorrowed =
            DataIdentifierBorrowed(markerAttributes = markerAttributes)

        /** Creates a [DataIdentifierBorrowed] for a borrowed [DataMarkerAttributes] and [DataLocale]. */
        fun forMarkerAttributesAndLocale(
            markerAttributes: DataMarkerAttributes,
            locale: DataLocale,
        ): DataIdentifierBorrowed =
            DataIdentifierBorrowed(
                markerAttributes = markerAttributes,
                locale = locale,
            )
    }
}

/**
 * A data identifier identifies a particular version of data, such as "English".
 *
 * It is a wrapper around a [DataLocale] and a [DataMarkerAttributes].
 */
data class DataIdentifierCow(
    /** Marker-specific request attributes. */
    val markerAttributes: DataMarkerAttributes = DataMarkerAttributes.empty(),
    /** The CLDR locale. */
    val locale: DataLocale = DataLocale(),
) : Comparable<DataIdentifierCow> {
    /** Borrows this [DataIdentifierCow] as a [DataIdentifierBorrowed]. */
    fun asBorrowed(): DataIdentifierBorrowed =
        DataIdentifierBorrowed(
            markerAttributes = markerAttributes,
            locale = locale,
        )

    /** Returns whether this id is equal to the default. */
    fun isUnknown(): Boolean = markerAttributes.isEmpty() && locale.isUnknown()

    override fun compareTo(other: DataIdentifierCow): Int {
        val markerAttributesComparison = markerAttributes.compareTo(other.markerAttributes)
        if (markerAttributesComparison != 0) {
            return markerAttributesComparison
        }
        return locale.totalCompare(other.locale)
    }

    override fun toString(): String =
        if (markerAttributes.isEmpty()) {
            locale.toString()
        } else {
            "$locale/${markerAttributes.asString()}"
        }

    companion object {
        /** Creates a [DataIdentifierCow] from an owned [DataLocale]. */
        fun fromLocale(locale: DataLocale): DataIdentifierCow =
            DataIdentifierCow(locale = locale)

        /** Creates a [DataIdentifierCow] from a borrowed [DataMarkerAttributes]. */
        fun fromMarkerAttributes(markerAttributes: DataMarkerAttributes): DataIdentifierCow =
            DataIdentifierCow(markerAttributes = markerAttributes)

        /** Creates a [DataIdentifierCow] from an owned [DataMarkerAttributes]. */
        fun fromMarkerAttributesOwned(markerAttributes: DataMarkerAttributes): DataIdentifierCow =
            DataIdentifierCow(markerAttributes = markerAttributes)

        /** Creates a [DataIdentifierCow] from an owned [DataMarkerAttributes] and an owned [DataLocale]. */
        fun fromOwned(
            markerAttributes: DataMarkerAttributes,
            locale: DataLocale,
        ): DataIdentifierCow =
            DataIdentifierCow(
                markerAttributes = markerAttributes,
                locale = locale,
            )

        /** Creates a [DataIdentifierCow] from a borrowed [DataMarkerAttributes] and an owned [DataLocale]. */
        fun fromBorrowedAndOwned(
            markerAttributes: DataMarkerAttributes,
            locale: DataLocale,
        ): DataIdentifierCow =
            DataIdentifierCow(
                markerAttributes = markerAttributes,
                locale = locale,
            )
    }
}

/**
 * An additional key to identify data beyond a [DataLocale].
 *
 * This is a loose wrapper around a string, with semantics defined by each [DataMarker].
 */
class DataMarkerAttributes private constructor(
    private val value: String,
) : Comparable<DataMarkerAttributes> {
    fun isEmpty(): Boolean = value.isEmpty()

    /** Returns this [DataMarkerAttributes] as a string. */
    fun asString(): String = value

    fun toOwned(): DataMarkerAttributes = DataMarkerAttributes(value)

    override fun compareTo(other: DataMarkerAttributes): Int =
        value.compareTo(other.value)

    override fun equals(other: Any?): Boolean =
        other is DataMarkerAttributes && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value

    companion object {
        private val EMPTY = DataMarkerAttributes("")

        private fun validate(codeUnits: ByteArray): Result<Unit> {
            for (codeUnitByte in codeUnits) {
                val codeUnit = codeUnitByte.toInt() and 0xFF
                if (
                    codeUnit !in 'a'.code..'z'.code &&
                    codeUnit !in 'A'.code..'Z'.code &&
                    codeUnit !in '0'.code..'9'.code &&
                    codeUnit != '-'.code &&
                    codeUnit != '_'.code
                ) {
                    return Result.failure(AttributeParseError())
                }
            }
            return Result.success(Unit)
        }

        /**
         * Creates a borrowed [DataMarkerAttributes] from a borrowed string.
         *
         * Returns an error if the string contains characters other than `[a-zA-Z0-9_-]`.
         */
        fun tryFromString(value: String): Result<DataMarkerAttributes> =
            tryFromUtf8(value.encodeToByteArray())

        /**
         * Attempts to create a borrowed [DataMarkerAttributes] from a borrowed UTF-8 encoded byte slice.
         *
         * Returns an error if the byte slice contains code units other than `[a-zA-Z0-9_-]`.
         */
        fun tryFromUtf8(codeUnits: ByteArray): Result<DataMarkerAttributes> {
            validate(codeUnits).getOrElse { error -> return Result.failure(error) }
            return Result.success(DataMarkerAttributes(codeUnits.decodeToString()))
        }

        /** Creates an owned [DataMarkerAttributes] from an owned string. */
        fun tryFromStringOwned(value: String): Result<DataMarkerAttributes> = tryFromString(value)

        /** Creates a borrowed [DataMarkerAttributes] from a borrowed string. */
        fun fromStringOrPanic(value: String): DataMarkerAttributes =
            tryFromString(value).getOrThrow()

        /** Creates an empty [DataMarkerAttributes]. */
        fun empty(): DataMarkerAttributes = EMPTY
    }
}

/** Invalid character. */
class AttributeParseError : Exception("invalid marker attribute syntax")
