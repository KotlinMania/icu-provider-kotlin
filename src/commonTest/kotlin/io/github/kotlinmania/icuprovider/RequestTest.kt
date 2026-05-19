// port-lint: source request.rs
package io.github.kotlinmania.icuprovider

import kotlin.test.Test
import kotlin.test.assertContentEquals

class RequestTest {
    @Test
    fun testDataMarkerAttributesFromUtf8() {
        val bytesList = listOf(
            "long-meter".encodeToByteArray(),
            "long".encodeToByteArray(),
            "meter".encodeToByteArray(),
            "short-meter-second".encodeToByteArray(),
            "usd".encodeToByteArray(),
        )

        for (bytes in bytesList) {
            val marker = DataMarkerAttributes.tryFromUtf8(bytes).getOrThrow()
            assertContentEquals(bytes, marker.toString().encodeToByteArray())
        }
    }
}
