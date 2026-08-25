// port-lint: source export/mod.rs
package io.github.kotlinmania.icuprovider.export

import io.github.kotlinmania.icuprovider.DataIdentifierBorrowed
import io.github.kotlinmania.icuprovider.DataMarkerInfo
import io.github.kotlinmania.icuprovider.DataPayload
import io.github.kotlinmania.icuprovider.IterableDynamicDataProvider

/** Contains information about a successful export. */
data class ExporterCloseMetadata(
    val metadata: Any? = null,
)

/** Metadata for [DataExporter.flush]. */
data class FlushMetadata(
    /** Whether the data was generated in such a way that a DryDataProvider implementation makes sense. */
    val supportsDryProvider: Boolean = false,
    /** The checksum to return with this data marker. */
    val checksum: ULong? = null,
)

/** An object capable of exporting data payloads in some form. */
interface DataExporter {
    /** Save a payload corresponding to the given marker and locale. */
    fun putPayload(
        marker: DataMarkerInfo,
        id: DataIdentifierBorrowed,
        payload: DataPayload<ExportMarker, ExportBox>,
    ): Result<Unit>

    /** Function called for singleton markers. */
    fun flushSingleton(
        marker: DataMarkerInfo,
        payload: DataPayload<ExportMarker, ExportBox>,
        metadata: FlushMetadata = FlushMetadata(),
    ): Result<Unit> {
        putPayload(marker, DataIdentifierBorrowed.default(), payload).getOrElse { return Result.failure(it) }
        return flush(marker, metadata)
    }

    /** Function called after a non-singleton marker has been fully enumerated. */
    fun flush(
        marker: DataMarkerInfo,
        metadata: FlushMetadata = FlushMetadata(),
    ): Result<Unit> = Result.success(Unit)

    /** Called before the object is closed. */
    fun close(): Result<ExporterCloseMetadata> = Result.success(ExporterCloseMetadata())
}

/** A dynamic data provider that can be used for exporting data. */
interface ExportableProvider : IterableDynamicDataProvider<ExportMarker, ExportBox> {
    /** Returns the set of supported markers. */
    fun supportedMarkers(): Set<DataMarkerInfo>
}

/** A [DataExporter] that forks to multiple [DataExporter]s. */
class MultiExporter(
    private val exporters: List<DataExporter> = emptyList(),
) : DataExporter {
    override fun putPayload(
        marker: DataMarkerInfo,
        id: DataIdentifierBorrowed,
        payload: DataPayload<ExportMarker, ExportBox>,
    ): Result<Unit> {
        for (exporter in exporters) {
            exporter.putPayload(marker, id, payload).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    override fun flushSingleton(
        marker: DataMarkerInfo,
        payload: DataPayload<ExportMarker, ExportBox>,
        metadata: FlushMetadata,
    ): Result<Unit> {
        for (exporter in exporters) {
            exporter.flushSingleton(marker, payload, metadata).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    override fun flush(
        marker: DataMarkerInfo,
        metadata: FlushMetadata,
    ): Result<Unit> {
        for (exporter in exporters) {
            exporter.flush(marker, metadata).getOrElse { return Result.failure(it) }
        }
        return Result.success(Unit)
    }

    override fun close(): Result<ExporterCloseMetadata> {
        val results = mutableListOf<ExporterCloseMetadata>()
        for (exporter in exporters) {
            val res = exporter.close().getOrElse { return Result.failure(it) }
            results.add(res)
        }
        return Result.success(ExporterCloseMetadata(results))
    }
}
