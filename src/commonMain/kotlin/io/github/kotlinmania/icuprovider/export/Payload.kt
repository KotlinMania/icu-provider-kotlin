// port-lint: source export/payload.rs
package io.github.kotlinmania.icuprovider.export

import io.github.kotlinmania.icuprovider.DataPayload
import io.github.kotlinmania.icuprovider.DynamicDataMarker

/** Trait for an exportable data payload. */
interface ExportableDataPayload {
    fun bakeSize(): Int

    fun asAny(): Any

    fun eqDyn(other: ExportableDataPayload): Boolean
}

/** Container box for exportable payloads. */
data class ExportBox(
    val payload: ExportableDataPayload,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExportBox) return false
        return payload.eqDyn(other.payload)
    }

    override fun hashCode(): Int = payload.hashCode()
}

/** Marker type for [ExportBox]. */
object ExportMarker : DynamicDataMarker<ExportBox>

/** Extension to convert any [DataPayload] to an [ExportMarker] payload. */
fun <M, DataStruct : Any> DataPayload<M, DataStruct>.intoExportPayload(): DataPayload<ExportMarker, ExportBox>
    where M : DynamicDataMarker<DataStruct> {
    val wrapped =
        object : ExportableDataPayload {
            override fun bakeSize(): Int = 0

            override fun asAny(): Any = this@intoExportPayload

            override fun eqDyn(other: ExportableDataPayload): Boolean {
                val otherPayload = other.asAny() as? DataPayload<*, *> ?: return false
                return this@intoExportPayload.get() == otherPayload.get()
            }
        }
    return DataPayload.fromOwned(ExportBox(wrapped))
}
