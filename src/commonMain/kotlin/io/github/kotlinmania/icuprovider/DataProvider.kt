// port-lint: source data_provider.rs
package io.github.kotlinmania.icuprovider

/** A data provider that loads data for a specific [DataMarkerInfo]. */
interface DataProvider<M, DataStruct : Any>
    where M : DataMarker<DataStruct> {
    /** Query the provider for data, returning the result. */
    fun load(request: DataRequest): Result<DataResponse<M, DataStruct>>
}

/** A data provider that can determine whether it can load a particular data identifier. */
interface DryDataProvider<M, DataStruct : Any> : DataProvider<M, DataStruct>
    where M : DataMarker<DataStruct> {
    /** This method goes through the motions of [load], but only returns the metadata. */
    fun dryLoad(request: DataRequest): Result<DataResponseMetadata>
}

/** A [DataProvider] that can iterate over all supported [DataIdentifierCow]s. */
interface IterableDataProvider<M, DataStruct : Any> : DataProvider<M, DataStruct>
    where M : DataMarker<DataStruct> {
    /** Returns a set of [DataIdentifierCow]. */
    fun iterIds(): Result<Set<DataIdentifierCow>>
}

/** A data provider that loads data for a specific data type. */
interface DynamicDataProvider<M, DataStruct : Any>
    where M : DynamicDataMarker<DataStruct> {
    /** Query the provider for data, returning the result. */
    fun loadData(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): Result<DataResponse<M, DataStruct>>
}

/** A dynamic data provider that can determine whether it can load a particular data identifier. */
interface DynamicDryDataProvider<M, DataStruct : Any> : DynamicDataProvider<M, DataStruct>
    where M : DynamicDataMarker<DataStruct> {
    /** This method goes through the motions of [loadData], but only returns the metadata. */
    fun dryLoadData(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): Result<DataResponseMetadata>
}

/** A [DynamicDataProvider] that can iterate over all supported [DataIdentifierCow]s for a certain marker. */
interface IterableDynamicDataProvider<M, DataStruct : Any> : DynamicDataProvider<M, DataStruct>
    where M : DynamicDataMarker<DataStruct> {
    /** Given a [DataMarkerInfo], returns a set of [DataIdentifierCow]. */
    fun iterIdsForMarker(marker: DataMarkerInfo): Result<Set<DataIdentifierCow>>
}

/** A data provider that loads data for a specific data type. */
interface BoundDataProvider<M, DataStruct : Any>
    where M : DynamicDataMarker<DataStruct> {
    /** Query the provider for data, returning the result. */
    fun loadBound(request: DataRequest): Result<DataResponse<M, DataStruct>>

    /** Returns the [DataMarkerInfo] that this provider uses for loading data. */
    fun boundMarker(): DataMarkerInfo
}

/** A [DataProvider] associated with a specific marker. */
data class DataProviderWithMarker<M, DataStruct : Any, P>(
    val inner: P,
    val marker: M,
) : BoundDataProvider<M, DataStruct>
    where M : DataMarker<DataStruct>,
          P : DataProvider<M, DataStruct> {
    /** Query the provider for data, returning the result. */
    override fun loadBound(request: DataRequest): Result<DataResponse<M, DataStruct>> =
        inner.load(request)

    /** Returns the [DataMarkerInfo] that this provider uses for loading data. */
    override fun boundMarker(): DataMarkerInfo = marker.info
}
