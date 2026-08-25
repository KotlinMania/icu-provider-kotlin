// port-lint: source response.rs
package io.github.kotlinmania.icuprovider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ResponseTest {
    @Test
    fun testCloneEq() {
        val p1 = DataPayload.fromStaticStr("Demo")
        val p2 = DataPayload.fromStaticStr("Demo")
        assertEquals(p1, p2)

        val po1: DataPayloadOr<HelloWorldV1, HelloWorld, Long> = DataPayloadOr.fromPayload(p1)
        val po2: DataPayloadOr<HelloWorldV1, HelloWorld, Long> = DataPayloadOr.fromPayload(p2)
        assertEquals(po1, po2)

        val po3: DataPayloadOr<HelloWorldV1, HelloWorld, Long> = DataPayloadOr.fromOther(555L)
        val po4: DataPayloadOr<HelloWorldV1, HelloWorld, Long> = DataPayloadOr.fromOther(555L)
        assertEquals(po3, po4)

        val po5: DataPayloadOr<HelloWorldV1, HelloWorld, Long> = DataPayloadOr.fromOther(666L)
        assertNotEquals(po3, po5)
        assertNotEquals(po4, po5)

        assertNotEquals(po1, po3)
        assertNotEquals(po1, po4)
        assertNotEquals(po1, po5)
        assertNotEquals(po2, po3)
        assertNotEquals(po2, po4)
        assertNotEquals(po2, po5)
    }

    @Test
    fun testWithMut() {
        val payload = DataPayload.fromOwned<HelloWorld, HelloWorldV1>(HelloWorld("Hello"))
        assertEquals("Hello", payload.get().message)
    }

    @Test
    fun testMapProject() {
        val p1 = DataPayload.fromOwned<HelloWorld, HelloWorldV1>(HelloWorld("Hello World"))
        assertEquals("Hello World", p1.get().message)

        val p2: DataPayload<HelloWorldV1, HelloWorld> = p1.mapProject { HelloWorld("${it.message} Extra") }
        assertEquals("Hello World Extra", p2.get().message)
    }
}
