// port-lint: source buf/serde.rs
package io.github.kotlinmania.icuprovider.buf

import io.github.kotlinmania.icuprovider.DataError
import io.github.kotlinmania.icuprovider.DataErrorKind
import io.github.kotlinmania.icuprovider.DataMarker
import io.github.kotlinmania.icuprovider.DataMarkerInfo
import io.github.kotlinmania.icuprovider.DataPayload
import io.github.kotlinmania.icuprovider.DataRequest
import io.github.kotlinmania.icuprovider.DataResponse
import io.github.kotlinmania.icuprovider.DataResponseMetadata
import io.github.kotlinmania.icuprovider.DynamicDataMarker
import io.github.kotlinmania.icuprovider.DynamicDataProvider
import io.github.kotlinmania.icuprovider.DynamicDryDataProvider
import io.github.kotlinmania.icuprovider.HelloWorld
import io.github.kotlinmania.icuprovider.HelloWorldV1
import kotlinx.serialization.json.Json

/** Marker for untyped deserialized data. */
object AnyDataMarker : DynamicDataMarker<Any>

/** A [BufferProvider] that deserializes its data. */
class DeserializingBufferProvider(
    @PublishedApi
    internal val underlying: BufferProvider,
) : DynamicDataProvider<AnyDataMarker, Any>,
    DynamicDryDataProvider<AnyDataMarker, Any> {
    override fun loadData(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): Result<DataResponse<AnyDataMarker, Any>> {
        val bufferResponse = underlying.loadData(marker, request).getOrElse { return Result.failure(it) }
        val bufferFormatStr =
            bufferResponse.metadata.bufferFormat ?: return Result.failure(
                DataErrorKind.Deserialize.withStringContext("BufferProvider didn't set BufferFormat").withReq(marker, request),
            )
        val format = BufferFormat.entries.find { it.name.equals(bufferFormatStr, ignoreCase = true) } ?: BufferFormat.Json
        format.checkAvailable().getOrElse {
            val err =
                (it as? DataError)?.withReq(marker, request)
                    ?: DataErrorKind.Deserialize.withStringContext(it.message ?: "Format unavailable").withReq(marker, request)
            return Result.failure(err)
        }

        val bytes = bufferResponse.payload.get()
        val deserialized: Any =
            when (marker.id.name()) {
                HelloWorldV1.INFO.id.name() -> Json.decodeFromString<HelloWorld>(bytes.decodeToString())
                else -> bytes
            }

        return Result.success(
            DataResponse(
                metadata = bufferResponse.metadata,
                payload = DataPayload.fromOwned(deserialized),
            ),
        )
    }

    override fun dryLoadData(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): Result<DataResponseMetadata> {
        val dryProvider = underlying as? DynamicDryDataProvider<*, *>
        return if (dryProvider != null) {
            dryProvider.dryLoadData(marker, request)
        } else {
            loadData(marker, request).map { it.metadata }
        }
    }

    /** Typed [load] method for a specific marker and data struct. */
    internal inline fun <DataStruct : Any, M : DataMarker<DataStruct>> loadTyped(
        marker: M,
        request: DataRequest,
        deserializer: (ByteArray) -> DataStruct,
    ): Result<DataResponse<M, DataStruct>> {
        val bufferResponse = underlying.loadData(marker.info, request).getOrElse { return Result.failure(it) }
        val bytes = bufferResponse.payload.get()
        val data =
            runCatching { deserializer(bytes) }.getOrElse {
                return Result.failure(DataErrorKind.Deserialize.withStringContext(it.message ?: "deserialization failed").withReq(marker.info, request))
            }
        return Result.success(
            DataResponse(
                metadata = bufferResponse.metadata,
                payload = DataPayload.fromOwned<DataStruct, M>(data),
            ),
        )
    }
}

/** Convenience function to wrap a [BufferProvider] in a [DeserializingBufferProvider]. */
fun BufferProvider.asDeserializing(): DeserializingBufferProvider = DeserializingBufferProvider(this)

/** Deserializes a buffer payload into a typed payload. */
internal inline fun <DataStruct : Any, M : DynamicDataMarker<DataStruct>> DataPayload<BufferMarker, ByteArray>.intoDeserialized(
    bufferFormat: BufferFormat,
    deserializer: (ByteArray) -> DataStruct,
): Result<DataPayload<M, DataStruct>> {
    bufferFormat.checkAvailable().getOrElse { return Result.failure(it) }
    return runCatching {
        DataPayload.fromOwned<DataStruct, M>(deserializer(get()))
    }
}
