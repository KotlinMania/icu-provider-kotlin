// port-lint: source buf.rs
package io.github.kotlinmania.icuprovider.buf

import io.github.kotlinmania.icuprovider.DataErrorKind
import io.github.kotlinmania.icuprovider.DynamicDataMarker
import io.github.kotlinmania.icuprovider.DynamicDataProvider

/** [DynamicDataMarker] for raw buffers. Returned by [BufferProvider]. */
object BufferMarker : DynamicDataMarker<ByteArray>

/** Data struct carried by [BufferMarker]. */
typealias DataStruct = ByteArray

/**
 * A data provider that returns opaque bytes.
 *
 * Generally, these bytes are expected to be deserializable. To get an object implementing the typed
 * data provider interface through deserialization, use the deserializing buffer-provider adapter.
 *
 * Passing a [BufferProvider] to a buffer-provider constructor requires enabling deserialization
 * support for the expected format or formats:
 *
 * - JSON
 * - Bincode, version 1
 * - Postcard, version 1
 *
 * Along with the typed data provider interface, this is one of the two foundational provider
 * interfaces in this package.
 *
 * [BufferProvider] can be made into an interface object. It is used over FFI.
 */
interface BufferProvider : DynamicDataProvider<BufferMarker, ByteArray>

/** An enum expressing all serialization formats known to ICU4X. */
enum class BufferFormat {
    /** Serialize using JavaScript Object Notation. */
    Json,

    /** Serialize using bincode, version 1. */
    Bincode1,

    /** Serialize using postcard, version 1. */
    Postcard1,
    ;

    /** Returns an error if the buffer format is not enabled. */
    fun checkAvailable(): Result<Unit> =
        when (this) {
            Json -> Result.success(Unit)
            Bincode1 -> Result.failure(
                DataErrorKind.Deserialize.withStringContext(
                    "deserializing BufferFormat.Bincode1 requires bincode version 1 support",
                ),
            )
            Postcard1 -> Result.failure(
                DataErrorKind.Deserialize.withStringContext(
                    "deserializing BufferFormat.Postcard1 requires postcard version 1 support",
                ),
            )
        }
}
