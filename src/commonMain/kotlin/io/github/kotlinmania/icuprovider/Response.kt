// port-lint: source response.rs
@file:Suppress("NOTHING_TO_INLINE")

package io.github.kotlinmania.icuprovider

import io.github.kotlinmania.icuprovider.buf.BufferMarker

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

    companion object {
        fun default(): DataResponseMetadata = DataResponseMetadata()
    }
}

/** A container for data payloads returned from a data provider. */
class DataPayload<M, DataStruct : Any>(
    @PublishedApi
    internal var value: DataStruct,
) where M : DynamicDataMarker<DataStruct> {
    /** Gets the data inside [DataPayload]. */
    fun get(): DataStruct = value

    /** Borrows the underlying data statically if possible. */
    fun getStatic(): DataStruct? = value

    /** Mutates the data contained in this DataPayload. */
    fun withMut(mutator: (DataStruct) -> Unit) {
        mutator(value)
    }

    /** Mutates the data stored in this [DataPayload] by replacing it with the mapped value. */
    inline fun <NewDataStruct : Any, M2> mapProject(mapper: (DataStruct) -> NewDataStruct): DataPayload<M2, NewDataStruct>
        where M2 : DynamicDataMarker<NewDataStruct> =
        DataPayload(mapper(value))

    /** Version of [mapProject] that borrows self instead of moving self. */
    inline fun <NewDataStruct : Any, M2> mapProjectCloned(mapper: (DataStruct) -> NewDataStruct): DataPayload<M2, NewDataStruct>
        where M2 : DynamicDataMarker<NewDataStruct> =
        DataPayload(mapper(value))

    /** Version of [mapProject] that bubbles up an error from mapper. */
    inline fun <NewDataStruct : Any, M2> tryMapProject(mapper: (DataStruct) -> Result<NewDataStruct>): Result<DataPayload<M2, NewDataStruct>>
        where M2 : DynamicDataMarker<NewDataStruct> =
        mapper(value).map { DataPayload(it) }

    /** Version of [mapProjectCloned] that bubbles up an error from mapper. */
    inline fun <NewDataStruct : Any, M2> tryMapProjectCloned(mapper: (DataStruct) -> Result<NewDataStruct>): Result<DataPayload<M2, NewDataStruct>>
        where M2 : DynamicDataMarker<NewDataStruct> =
        mapper(value).map { DataPayload(it) }

    /** Casts this [DataPayload] to a different marker with the same data struct. */
    fun <M2> cast(): DataPayload<M2, DataStruct>
        where M2 : DynamicDataMarker<DataStruct> =
        DataPayload(value)

    /** Casts reference to this [DataPayload] to a different marker with the same data struct. */
    fun <M2> castRef(): DataPayload<M2, DataStruct>
        where M2 : DynamicDataMarker<DataStruct> =
        DataPayload(value)

    /** Converts a [DataPayload] to one of the same type with runtime type checking. */
    inline fun <reified M2, reified TargetDataStruct : Any> dynamicCast(): Result<DataPayload<M2, TargetDataStruct>>
        where M2 : DynamicDataMarker<TargetDataStruct> =
        if (value is TargetDataStruct) {
            @Suppress("UNCHECKED_CAST")
            Result.success(this as DataPayload<M2, TargetDataStruct>)
        } else {
            Result.failure(DataError.forType(TargetDataStruct::class.simpleName ?: "Unknown"))
        }

    /** Convert a mutable reference of a [DataPayload] to another mutable reference of the same type with runtime type checking. */
    inline fun <reified M2, reified TargetDataStruct : Any> dynamicCastMut(): Result<DataPayload<M2, TargetDataStruct>>
        where M2 : DynamicDataMarker<TargetDataStruct> =
        dynamicCast<M2, TargetDataStruct>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataPayload<*, *>) return false
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    companion object {
        /** Creates a [DataPayload] from owned data. */
        fun <M, DataStruct : Any> fromOwned(value: DataStruct): DataPayload<M, DataStruct>
            where M : DynamicDataMarker<DataStruct> =
            DataPayload(value)

        /** Creates a [DataPayload] from a static reference. */
        fun <M, DataStruct : Any> fromStaticRef(value: DataStruct): DataPayload<M, DataStruct>
            where M : DynamicDataMarker<DataStruct> =
            DataPayload(value)

        /** Creates a [DataPayload] containing a static string for [HelloWorld]. */
        fun fromStaticStr(str: String): DataPayload<HelloWorldV1, HelloWorld> =
            DataPayload(HelloWorld(str))

        /** Converts an owned byte buffer into a DataPayload<BufferMarker, ByteArray>. */
        fun fromOwnedBuffer(buffer: ByteArray): DataPayload<BufferMarker, ByteArray> =
            DataPayload(buffer)

        /** Converts a static byte buffer into a DataPayload<BufferMarker, ByteArray>. */
        fun fromStaticBuffer(buffer: ByteArray): DataPayload<BufferMarker, ByteArray> =
            DataPayload(buffer)

        /** Converts a yoked byte buffer into a DataPayload<BufferMarker, ByteArray>. */
        fun fromYokedBuffer(buffer: ByteArray): DataPayload<BufferMarker, ByteArray> =
            DataPayload(buffer)
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

    /** Returns whether this object represents a [DataPayload]. */
    fun isPayload(): Boolean = this is Payload

    fun get(): Result<DataStruct> =
        when (this) {
            is Payload -> Result.success(payload.get())
            is Other -> Result.failure(DataPayloadOtherError(other.toString()))
        }

    fun intoInner(): Result<DataPayload<M, DataStruct>> =
        when (this) {
            is Payload -> Result.success(payload)
            is Other -> Result.failure(DataPayloadOtherError(other.toString()))
        }

    fun getOption(): DataStruct? =
        when (this) {
            is Payload -> payload.get()
            is Other -> null
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

        /** Convenience function to return the other type with value [Unit]. */
        fun <M, DataStruct : Any> none(): DataPayloadOr<M, DataStruct, Unit>
            where M : DynamicDataMarker<DataStruct> =
            Other(Unit)
    }
}

class DataPayloadOtherError(
    message: String,
) : Exception(message)

/** The type of the cart that is used by [DataPayload]. */
data class Cart(
    val bytes: ByteArray = byteArrayOf(),
) {
    /** Creates a yoke-like payload by applying [mapper] to owned bytes. */
    fun <Y : Any> tryMakeYoke(mapper: (ByteArray) -> Y): Result<Y> =
        runCatching { mapper(bytes.copyOf()) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cart) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = bytes.contentHashCode()

    companion object {
        fun <Y : Any> unwrapCart(cart: Cart, mapper: (ByteArray) -> Y): Y = mapper(cart.bytes)
    }
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

    /** Converts a [DataResponse] to one of the same type with runtime type checking. */
    inline fun <reified M2, reified TargetDataStruct : Any> dynamicCast(): Result<DataResponse<M2, TargetDataStruct>>
        where M2 : DynamicDataMarker<TargetDataStruct> =
        payload.dynamicCast<M2, TargetDataStruct>().map {
            DataResponse(
                metadata = metadata,
                payload = it,
            )
        }
}
