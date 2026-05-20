// port-lint: source error.rs
package io.github.kotlinmania.icuprovider

/**
 * A list specifying general categories of data provider error.
 *
 * Errors may be caused either by a malformed request or by the data provider not being able to
 * fulfill a well-formed request.
 */
sealed class DataErrorKind {
    /** No data for the requested data marker. This is only returned by [DynamicDataProvider]. */
    data object MarkerNotFound : DataErrorKind()

    /** There is data for the data marker, but not for this particular data identifier. */
    data object IdentifierNotFound : DataErrorKind()

    /** The request is invalid, such as a request for a singleton marker containing a data identifier. */
    data object InvalidRequest : DataErrorKind()

    /** The data for two [DataMarker]s is not consistent. */
    data class InconsistentData(val marker: DataMarkerInfo) : DataErrorKind()

    /** An error occurred during downcasting. */
    data class Downcast(val expected: String) : DataErrorKind()

    /** An error occurred during deserialization. */
    data object Deserialize : DataErrorKind()

    /** An unspecified error occurred. */
    data object Custom : DataErrorKind()

    /** An error occurred while accessing a system resource. */
    data class Io(val kind: String) : DataErrorKind()

    /** Converts this [DataErrorKind] into a [DataError]. */
    fun intoError(): DataError =
        DataError(
            kind = this,
            marker = null,
            stringContext = null,
            silent = false,
        )

    /** Creates a [DataError] with a data marker context. */
    fun withMarker(marker: DataMarkerInfo): DataError = intoError().withMarker(marker)

    /** Creates a [DataError] with a string context. */
    fun withStringContext(context: String): DataError = intoError().withStringContext(context)

    /** Creates a [DataError] with a string context. */
    fun withStrContext(context: String): DataError = withStringContext(context)

    /** Creates a [DataError] with a type name context. */
    fun withTypeContext(typeName: String): DataError = intoError().withTypeContext(typeName)

    /** Creates a [DataError] with a request context. */
    fun withReq(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): DataError = intoError().withReq(marker, request)

    fun fmt(): String = toString()

    override fun toString(): String =
        when (this) {
            MarkerNotFound -> "Missing data for marker"
            IdentifierNotFound -> "Missing data for identifier"
            InvalidRequest -> "Invalid request"
            is InconsistentData -> "The data for two markers is not consistent: $marker"
            is Downcast -> "Downcast: expected $expected, found"
            Deserialize -> "Deserialize"
            Custom -> "Custom"
            is Io -> "I/O: $kind"
        }
}

/**
 * The error type for ICU4X data provider operations.
 *
 * To create one of these, either start with a [DataErrorKind] or use [custom].
 */
data class DataError(
    /** Broad category of the error. */
    val kind: DataErrorKind,
    /** The data marker of the request, if available. */
    val marker: DataMarkerId? = null,
    /** Additional context, if available. */
    val stringContext: String? = null,
    /** Whether this error was created in silent mode to not log. */
    val silent: Boolean = false,
) : Exception() {
    /** Sets the data marker of a [DataError], returning a modified error. */
    fun withMarker(marker: DataMarkerInfo): DataError =
        copy(marker = marker.id)

    /** Sets the string context of a [DataError], returning a modified error. */
    fun withStringContext(context: String): DataError =
        copy(stringContext = context)

    /** Sets the string context of a [DataError], returning a modified error. */
    fun withStrContext(context: String): DataError = withStringContext(context)

    /** Sets the string context of a [DataError] to the given type name, returning a modified error. */
    fun withTypeContext(typeName: String): DataError = withStringContext(typeName)

    /** Logs the data error with the given request, returning an error containing the data marker. */
    fun withReq(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): DataError {
        val error = if (request.metadata.silent) copy(silent = true) else this
        return error.withMarker(marker)
    }

    /** Logs the data error with the given context, then return self. */
    fun withPathContext(path: String): DataError {
        path.length
        return this
    }

    /** Logs the data error with the given context, then return self. */
    fun withDisplayContext(context: Any): DataError =
        if (silent) this else withStringContext(context.toString())

    /** Logs the data error with the given context, then return self. */
    fun withDebugContext(context: Any): DataError =
        if (silent) this else withStringContext(context.toString())

    override val message: String
        get() {
            val builder = StringBuilder("ICU4X data error")
            if (kind != DataErrorKind.Custom) {
                builder.append(": ").append(kind)
            }
            marker?.let { builder.append(" (marker: ").append(it).append(")") }
            stringContext?.let { builder.append(": ").append(it) }
            return builder.toString()
        }

    fun fmt(): String = message

    companion object {
        /** Returns a new, empty [DataError] with kind [DataErrorKind.Custom] and a string error message. */
        fun custom(stringContext: String): DataError =
            DataError(
                kind = DataErrorKind.Custom,
                marker = null,
                stringContext = stringContext,
                silent = false,
            )

        fun forType(typeName: String): DataError =
            DataError(
                kind = DataErrorKind.Downcast(typeName),
                marker = null,
                stringContext = null,
                silent = false,
            )

        fun from(error: Throwable): DataError =
            DataError(
                kind = DataErrorKind.Io(error.message ?: error.toString()),
                marker = null,
                stringContext = null,
                silent = false,
            )
    }
}

/** Extension shape for [Result] values carrying [DataError]. */
class ResultDataError<T>(
    private val result: Result<T>,
) {
    /** Propagates all errors other than [DataErrorKind.IdentifierNotFound], and returns null in that case. */
    fun allowIdentifierNotFound(): Result<T?> = result.allowIdentifierNotFound()
}

/** Propagates all errors other than [DataErrorKind.IdentifierNotFound], and returns null in that case. */
fun <T> Result<T>.allowIdentifierNotFound(): Result<T?> =
    fold(
        onSuccess = { value -> Result.success(value) },
        onFailure = { error ->
            if (error is DataError && error.kind == DataErrorKind.IdentifierNotFound) {
                Result.success(null)
            } else {
                Result.failure(error)
            }
        },
    )
