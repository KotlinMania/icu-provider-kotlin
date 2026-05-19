// port-lint: source request.rs
package io.github.kotlinmania.icuprovider

/** Invalid character in marker attributes. */
class AttributeParseError : Exception("invalid marker attribute syntax")

/**
 * An additional key to identify data beyond a data locale.
 *
 * This is a loose wrapper around a string, with semantics defined by each data marker.
 */
class DataMarkerAttributes private constructor(
    private val value: String,
) : Comparable<DataMarkerAttributes> {
    /** Returns these [DataMarkerAttributes] as a string. */
    fun asString(): String = value

    fun isEmpty(): Boolean = value.isEmpty()

    override fun compareTo(other: DataMarkerAttributes): Int =
        value.compareTo(other.value)

    override fun equals(other: Any?): Boolean =
        other is DataMarkerAttributes && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value

    companion object {
        private val EMPTY = DataMarkerAttributes("")

        private fun validate(codeUnits: ByteArray): Boolean =
            codeUnits.all { byte ->
                val codeUnit = byte.toInt() and 0xFF
                codeUnit in 'a'.code..'z'.code ||
                    codeUnit in 'A'.code..'Z'.code ||
                    codeUnit in '0'.code..'9'.code ||
                    codeUnit == '-'.code ||
                    codeUnit == '_'.code
            }

        /**
         * Creates [DataMarkerAttributes] from a string.
         *
         * Returns an error if the string contains characters other than `[a-zA-Z0-9_-]`.
         */
        fun tryFromString(value: String): Result<DataMarkerAttributes> =
            tryFromUtf8(value.encodeToByteArray())

        /**
         * Attempts to create [DataMarkerAttributes] from UTF-8 encoded code units.
         *
         * Returns an error if the byte slice contains code units other than `[a-zA-Z0-9_-]`.
         */
        fun tryFromUtf8(codeUnits: ByteArray): Result<DataMarkerAttributes> {
            if (!validate(codeUnits)) {
                return Result.failure(AttributeParseError())
            }
            return Result.success(DataMarkerAttributes(codeUnits.decodeToString()))
        }

        /** Creates [DataMarkerAttributes] from a string, or panics if syntax is invalid. */
        fun fromStringOrPanic(value: String): DataMarkerAttributes =
            tryFromString(value).getOrThrow()

        /** Creates an empty [DataMarkerAttributes]. */
        fun empty(): DataMarkerAttributes = EMPTY
    }
}
