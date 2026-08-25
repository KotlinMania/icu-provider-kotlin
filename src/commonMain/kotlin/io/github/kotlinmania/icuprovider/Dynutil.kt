// port-lint: source dynutil.rs
package io.github.kotlinmania.icuprovider

/**
 * Trait to allow conversion from `DataPayload<M, S>` to `DataPayload<TargetM, S>`.
 */
interface UpcastDataPayload<M, S : Any, TargetM>
    where M : DynamicDataMarker<S>,
          TargetM : DynamicDataMarker<S> {
    /** Upcast a [DataPayload] to another payload with the same data struct. */
    fun upcast(other: DataPayload<M, S>): DataPayload<TargetM, S>
}

/** Implementation of [UpcastDataPayload] that casts directly. */
class DirectUpcast<M, S : Any, TargetM> : UpcastDataPayload<M, S, TargetM>
    where M : DynamicDataMarker<S>,
          TargetM : DynamicDataMarker<S> {
    override fun upcast(other: DataPayload<M, S>): DataPayload<TargetM, S> = other.cast()
}
