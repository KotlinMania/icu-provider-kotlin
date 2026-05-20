// port-lint: source baked.rs
package io.github.kotlinmania.icuprovider.baked

import io.github.kotlinmania.icuprovider.DataIdentifierBorrowed
import io.github.kotlinmania.icuprovider.DataIdentifierCow
import io.github.kotlinmania.icuprovider.DataMarker
import io.github.kotlinmania.icuprovider.DataPayload

/**
 * This package contains scaffolding for baked providers, typically generated using databake.
 *
 * It can be wildcard-imported, and includes the icu-provider prelude.
 *
 * This needs baked-provider support to be enabled.
 */

/** A backing store for baked data. */
interface DataStore<M, DataStruct : Any>
    : Sealed
    where M : DataMarker<DataStruct> {
    /** Get the value for a key. */
    fun get(
        req: DataIdentifierBorrowed,
        attributesPrefixMatch: Boolean,
    ): DataPayload<M, DataStruct>?

    /** Iterate over all data. */
    fun iter(): Iterator<DataIdentifierCow>
}

/** Sealed marker for baked data stores. */
interface Sealed
