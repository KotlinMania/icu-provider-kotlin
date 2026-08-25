// port-lint: source export/payload.rs
package io.github.kotlinmania.icuprovider

import io.github.kotlinmania.icuprovider.export.ExportBox
import io.github.kotlinmania.icuprovider.export.ExportMarker
import io.github.kotlinmania.icuprovider.export.intoExportPayload
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ExportTest {
    @Test
    fun testCompareWithDyn() {
        val payload1: DataPayload<HelloWorldV1, HelloWorld> = DataPayload.fromOwned(HelloWorld("abc"))
        val payload2: DataPayload<HelloWorldV1, HelloWorld> = DataPayload.fromOwned(HelloWorld("abc"))
        val payload3: DataPayload<HelloWorldV1, HelloWorld> = DataPayload.fromOwned(HelloWorld("def"))

        assertEquals(payload1, payload2)
        assertEquals(payload2, payload1)

        assertNotEquals(payload1, payload3)
        assertNotEquals(payload3, payload1)
    }

    @Test
    fun testExportMarkerPartialEq() {
        val payload1: DataPayload<ExportMarker, ExportBox> =
            DataPayload.fromOwned<HelloWorld, HelloWorldV1>(HelloWorld("abc")).intoExportPayload()
        val payload2: DataPayload<ExportMarker, ExportBox> =
            DataPayload.fromOwned<HelloWorld, HelloWorldV1>(HelloWorld("abc")).intoExportPayload()
        val payload3: DataPayload<ExportMarker, ExportBox> =
            DataPayload.fromOwned<HelloWorld, HelloWorldV1>(HelloWorld("def")).intoExportPayload()

        assertEquals(payload1, payload2)
        assertEquals(payload2, payload1)
        assertNotEquals(payload1, payload3)
        assertNotEquals(payload3, payload1)
    }
}
