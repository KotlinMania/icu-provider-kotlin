// port-lint: source response.rs
package io.github.kotlinmania.icuprovider

/** A response object containing metadata about the returned data. */
data class DataResponseMetadata(
    /** The resolved locale of the returned data, if locale fallbacking was performed. */
    val locale: DataLocale? = null,
    /** The format of the buffer for buffer-backed data, if known. */
    val bufferFormat: String? = null,
    /** An optional checksum. This can be used to ensure consistency across different markers. */
    val checksum: ULong? = null,
) {
    /** Sets the checksum. */
    fun withChecksum(checksum: ULong): DataResponseMetadata = copy(checksum = checksum)
}

/** A container for data payloads returned from a data provider. */
class DataPayload<M, DataStruct : Any>(
    private val value: DataStruct,
) where M : DynamicDataMarker<DataStruct> {
    /** Gets the data inside [DataPayload]. */
    fun get(): DataStruct = value

    /** Mutates the data stored in this [DataPayload] by replacing it with the mapped value. */
    fun <NewDataStruct : Any, M2> mapProject(mapper: (DataStruct) -> NewDataStruct): DataPayload<M2, NewDataStruct>
        where M2 : DynamicDataMarker<NewDataStruct> =
        DataPayload(mapper(value))

    /** Casts this [DataPayload] to a different marker with the same data struct. */
    fun <M2> cast(): DataPayload<M2, DataStruct>
        where M2 : DynamicDataMarker<DataStruct> =
        DataPayload(value)

    companion object {
        /** Creates a [DataPayload] from owned data. */
        fun <M, DataStruct : Any> fromOwned(value: DataStruct): DataPayload<M, DataStruct>
            where M : DynamicDataMarker<DataStruct> =
            DataPayload(value)

        /** Creates a [DataPayload] from a static reference. */
        fun <M, DataStruct : Any> fromStaticRef(value: DataStruct): DataPayload<M, DataStruct>
            where M : DynamicDataMarker<DataStruct> =
            DataPayload(value)
    }
}

/** A container for data payloads with storage for something else. */
sealed class DataPayloadOr<M, DataStruct : Any, O>
    where M : DynamicDataMarker<DataStruct> {
    data class Payload<M, DataStruct : Any, O>(
        val payload: DataPayload<M, DataStruct>,
    ) : DataPayloadOr<M, DataStruct, O>() where M : DynamicDataMarker<DataStruct>

    data class Other<M, DataStruct : Any, O>(
        val other: O,
    ) : DataPayloadOr<M, DataStruct, O>() where M : DynamicDataMarker<DataStruct>

    fun get(): Result<DataStruct> =
        when (this) {
            is Payload -> Result.success(payload.get())
            is Other -> Result.failure(DataPayloadOtherError(other.toString()))
        }

    companion object {
        /** Creates [DataPayloadOr] from a payload. */
        fun <M, DataStruct : Any, O> fromPayload(payload: DataPayload<M, DataStruct>): DataPayloadOr<M, DataStruct, O>
            where M : DynamicDataMarker<DataStruct> =
            Payload(payload)

        /** Creates [DataPayloadOr] from other storage. */
        fun <M, DataStruct : Any, O> fromOther(other: O): DataPayloadOr<M, DataStruct, O>
            where M : DynamicDataMarker<DataStruct> =
            Other(other)
    }
}

class DataPayloadOtherError(message: String) : Exception(message)

/** The type of the cart that is used by [DataPayload]. */
data class Cart(
    val bytes: ByteArray = byteArrayOf(),
) {
    /** Creates a yoke-like payload by applying [mapper] to owned bytes. */
    fun <Y : Any> tryMakeYoke(mapper: (ByteArray) -> Y): Result<Y> =
        runCatching { mapper(bytes.copyOf()) }
}

/** A response from a data provider. */
data class DataResponse<M, DataStruct : Any>(
    /** Metadata about the response. */
    val metadata: DataResponseMetadata = DataResponseMetadata(),
    /** Data payload for the response. */
    val payload: DataPayload<M, DataStruct>,
) where M : DynamicDataMarker<DataStruct> {
    /** Casts this [DataResponse] to another marker with the same data struct. */
    fun <M2> cast(): DataResponse<M2, DataStruct>
        where M2 : DynamicDataMarker<DataStruct> =
        DataResponse(
            metadata = metadata,
            payload = payload.cast(),
        )
}
