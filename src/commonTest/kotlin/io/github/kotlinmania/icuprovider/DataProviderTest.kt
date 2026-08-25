// port-lint: source data_provider.rs
package io.github.kotlinmania.icuprovider

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@Serializable
data class HelloAlt(
    val message: String = "",
)

object HelloAltMarkerV1 : DataMarker<HelloAlt> {
    override val info: DataMarkerInfo =
        dataMarker<HelloAlt>(
            name = "HelloAltMarkerV1",
            hasChecksum = false,
        )
}

@Serializable
data class HelloCombined(
    val helloV1: HelloWorld,
    val helloAlt: HelloAlt,
)

data class DataWarehouse(
    val helloV1: HelloWorld,
    val helloAlt: HelloAlt,
) : DataProvider<HelloWorldV1, HelloWorld> {
    override fun load(request: DataRequest): Result<DataResponse<HelloWorldV1, HelloWorld>> =
        Result.success(
            DataResponse(
                metadata = DataResponseMetadata.default(),
                payload = DataPayload.fromOwned(helloV1),
            ),
        )
}

class DataProvider2(
    val data: DataWarehouse,
) : DataProvider<HelloWorldV1, HelloWorld> {
    override fun load(request: DataRequest): Result<DataResponse<HelloWorldV1, HelloWorld>> =
        Result.success(
            DataResponse(
                metadata = DataResponseMetadata.default(),
                payload = DataPayload.fromOwned(data.helloV1),
            ),
        )

    fun loadAlt(request: DataRequest): Result<DataResponse<HelloAltMarkerV1, HelloAlt>> =
        Result.success(
            DataResponse(
                metadata = DataResponseMetadata.default(),
                payload = DataPayload.fromOwned(data.helloAlt),
            ),
        )

    companion object {
        fun fromWarehouse(warehouse: DataWarehouse): DataProvider2 = DataProvider2(warehouse)
    }
}

class DataProviderTest {
    private val dataJson =
        """
        {
            "helloV1": {
                "message": "Hello "
            },
            "helloAlt": {
                "message": "Hello Alt"
            }
        }
        """.trimIndent()

    private fun getWarehouse(json: String): DataWarehouse {
        val combined = Json.decodeFromString<HelloCombined>(json)
        return DataWarehouse(
            helloV1 = combined.helloV1,
            helloAlt = combined.helloAlt,
        )
    }

    private fun getPayloadV1(provider: DataProvider<HelloWorldV1, HelloWorld>): Result<DataPayload<HelloWorldV1, HelloWorld>> =
        provider.load(DataRequest(id = DataIdentifierBorrowed.default())).map { it.payload }

    @Test
    fun testWarehouseOwned() {
        val warehouse = getWarehouse(dataJson)
        val helloData = getPayloadV1(warehouse).getOrThrow()
        assertEquals("Hello ", helloData.get().message)
    }

    @Test
    fun testWarehouseOwnedDynGeneric() {
        val warehouse: DataProvider<HelloWorldV1, HelloWorld> = getWarehouse(dataJson)
        val helloData = getPayloadV1(warehouse).getOrThrow()
        assertEquals("Hello ", helloData.get().message)
    }

    @Test
    fun testProvider2() {
        val warehouse = getWarehouse(dataJson)
        val provider = DataProvider2.fromWarehouse(warehouse)
        val helloData = getPayloadV1(provider).getOrThrow()
        assertEquals("Hello ", helloData.get().message)
    }

    @Test
    fun testProvider2DynGeneric() {
        val warehouse = getWarehouse(dataJson)
        val provider: DataProvider<HelloWorldV1, HelloWorld> = DataProvider2.fromWarehouse(warehouse)
        val helloData = getPayloadV1(provider).getOrThrow()
        assertEquals("Hello ", helloData.get().message)
    }

    @Test
    fun testProvider2DynGenericAlt() {
        val warehouse = getWarehouse(dataJson)
        val provider = DataProvider2.fromWarehouse(warehouse)
        val helloData = provider.loadAlt(DataRequest(id = DataIdentifierBorrowed.default())).getOrThrow().payload
        assertEquals("Hello Alt", helloData.get().message)
    }

    @Test
    fun testV1V2Generic() {
        val warehouse = getWarehouse(dataJson)
        val provider = DataProvider2.fromWarehouse(warehouse)
        val v1 = provider.load(DataRequest(id = DataIdentifierBorrowed.default())).getOrThrow().payload
        val v2 = provider.loadAlt(DataRequest(id = DataIdentifierBorrowed.default())).getOrThrow().payload
        assertNotEquals(v1.get().message, v2.get().message)
    }
}
