// port-lint: source baked/zerotrie.rs
@file:Suppress("NOTHING_TO_INLINE")

package io.github.kotlinmania.icuprovider.baked

import io.github.kotlinmania.icuprovider.DataIdentifierBorrowed
import io.github.kotlinmania.icuprovider.DataIdentifierCow
import io.github.kotlinmania.icuprovider.DataLocale
import io.github.kotlinmania.icuprovider.DataMarker
import io.github.kotlinmania.icuprovider.DataMarkerAttributes
import io.github.kotlinmania.icuprovider.DataPayload

/** This is a valid separator as DataLocale will never produce it. */
const val ID_SEPARATOR: Byte = 0x1E

/** Regular baked data: a map/table for lookups and a list of values. */
class Data<M, DataStruct : Any>(
    private val entries: Map<String, DataStruct>,
) : DataStore<M, DataStruct>
    where M : DataMarker<DataStruct> {
    override fun get(
        req: DataIdentifierBorrowed,
        attributesPrefixMatch: Boolean,
    ): DataPayload<M, DataStruct>? {
        val key =
            if (req.markerAttributes.isEmpty()) {
                req.locale.toString()
            } else {
                "${req.locale}${ID_SEPARATOR.toInt().toChar()}${req.markerAttributes}"
            }
        val value =
            entries[key] ?: if (attributesPrefixMatch) {
                val prefix = "${req.locale}${ID_SEPARATOR.toInt().toChar()}"
                entries.entries.firstOrNull { it.key.startsWith(prefix) }?.value
            } else {
                null
            }
        return value?.let { DataPayload.fromStaticRef(it) }
    }

    override fun iter(): Iterator<DataIdentifierCow> =
        entries.keys
            .map { key ->
                val parts = key.split(ID_SEPARATOR.toInt().toChar(), limit = 2)
                if (parts.size == 2) {
                    DataIdentifierCow.fromOwned(
                        DataMarkerAttributes.fromStringOrPanic(parts[1]),
                        DataLocale(parts[0]),
                    )
                } else {
                    DataIdentifierCow.fromLocale(DataLocale(parts[0]))
                }
            }.iterator()

    companion object {
        fun <M, DataStruct : Any> fromEntries(entries: Map<String, DataStruct>): Data<M, DataStruct>
            where M : DataMarker<DataStruct> = Data(entries)
    }
}

/** Regular baked data: a lookup table with references to values. */
class DataRef<M, DataStruct : Any>(
    private val entries: Map<String, DataStruct>,
) : DataStore<M, DataStruct>
    where M : DataMarker<DataStruct> {
    override fun get(
        req: DataIdentifierBorrowed,
        attributesPrefixMatch: Boolean,
    ): DataPayload<M, DataStruct>? {
        val key =
            if (req.markerAttributes.isEmpty()) {
                req.locale.toString()
            } else {
                "${req.locale}${ID_SEPARATOR.toInt().toChar()}${req.markerAttributes}"
            }
        val value = entries[key]
        return value?.let { DataPayload.fromStaticRef(it) }
    }

    override fun iter(): Iterator<DataIdentifierCow> =
        entries.keys
            .map { key ->
                val parts = key.split(ID_SEPARATOR.toInt().toChar(), limit = 2)
                if (parts.size == 2) {
                    DataIdentifierCow.fromOwned(
                        DataMarkerAttributes.fromStringOrPanic(parts[1]),
                        DataLocale(parts[0]),
                    )
                } else {
                    DataIdentifierCow.fromLocale(DataLocale(parts[0]))
                }
            }.iterator()
}

/** Optimized data stored as VarULEs. */
class DataForVarULEs<M, DataStruct : Any, EncodedStruct>(
    private val entries: Map<String, DataStruct>,
) : DataStore<M, DataStruct>
    where M : DataMarker<DataStruct> {
    override fun get(
        req: DataIdentifierBorrowed,
        attributesPrefixMatch: Boolean,
    ): DataPayload<M, DataStruct>? {
        val key =
            if (req.markerAttributes.isEmpty()) {
                req.locale.toString()
            } else {
                "${req.locale}${ID_SEPARATOR.toInt().toChar()}${req.markerAttributes}"
            }
        val value = entries[key]
        return value?.let { DataPayload.fromOwned(it) }
    }

    override fun iter(): Iterator<DataIdentifierCow> =
        entries.keys
            .map { key ->
                val parts = key.split(ID_SEPARATOR.toInt().toChar(), limit = 2)
                if (parts.size == 2) {
                    DataIdentifierCow.fromOwned(
                        DataMarkerAttributes.fromStringOrPanic(parts[1]),
                        DataLocale(parts[0]),
                    )
                } else {
                    DataIdentifierCow.fromLocale(DataLocale(parts[0]))
                }
            }.iterator()
}
