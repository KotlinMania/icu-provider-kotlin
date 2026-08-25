// port-lint: source varule_traits.rs
package io.github.kotlinmania.icuprovider

/**
 * A trait that associates a VarULE type with a data struct.
 *
 * Some data structs can be represented compactly as a single VarULE,
 * such as `String` or a packed pattern. This trait allows for data providers
 * to use optimizations for such types.
 */
interface MaybeAsVarULE<EncodedStruct>

/**
 * Export-only trait associated with [MaybeAsVarULE].
 */
interface MaybeEncodeAsVarULE<EncodedStruct> : MaybeAsVarULE<EncodedStruct> {
    /**
     * Returns the [EncodedStruct] that represents this data struct,
     * or null if the data struct does not support this representation.
     */
    fun maybeEncodeAsVarule(): EncodedStruct?
}
