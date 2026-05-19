// port-lint: source marker.rs
package io.github.kotlinmania.icuprovider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MarkerTest {
    @Test
    fun testMarkerSyntax() {
        // Valid markers:
        DataMarkerId.fromName("HelloWorldV1").getOrThrow()
        DataMarkerId.fromName("HelloWorldFooV1").getOrThrow()
        DataMarkerId.fromName("HelloWorldV999").getOrThrow()
        DataMarkerId.fromName("Hello485FooV1").getOrThrow()

        // No version:
        assertNameError("[0-9]", "HelloWorld".length, DataMarkerId.fromName("HelloWorld"))
        assertNameError("[0-9]", "HelloWorldV".length, DataMarkerId.fromName("HelloWorldV"))
        assertNameError("[0-9]", "HelloWorldVFoo".length, DataMarkerId.fromName("HelloWorldVFoo"))
        assertNameError("[0-9]", "HelloWorldV1Foo".length, DataMarkerId.fromName("HelloWorldV1Foo"))
    }

    @Test
    fun testIdDebug() {
        assertEquals("BarV1", DataMarkerId.fromName("BarV1").getOrThrow().debug)
    }

    @Test
    fun testHashWord32() {
        assertEquals(0x0000_0000L, fxhash32("".encodeToByteArray()))
        assertEquals(0xF305_1F19L, fxhash32("a".encodeToByteArray()))
        assertEquals(0x2F9D_F119L, fxhash32("ab".encodeToByteArray()))
        assertEquals(0xCB1D_9396L, fxhash32("abc".encodeToByteArray()))
        assertEquals(0x8628_F119L, fxhash32("abcd".encodeToByteArray()))
        assertEquals(0xBEBD_B56DL, fxhash32("abcde".encodeToByteArray()))
        assertEquals(0x1CE8_476DL, fxhash32("abcdef".encodeToByteArray()))
        assertEquals(0xC0F1_76A4L, fxhash32("abcdefg".encodeToByteArray()))
        assertEquals(0x09AB_476DL, fxhash32("abcdefgh".encodeToByteArray()))
        assertEquals(0xB72F_5D88L, fxhash32("abcdefghi".encodeToByteArray()))
    }

    @Test
    fun testIdHash() {
        assertEquals(
            listOf(212, 77, 158, 241),
            DataMarkerId.fromName("BarV1").getOrThrow().hashed().toBytes().toUnsignedInts(),
        )
    }

    private fun assertNameError(
        expected: String,
        index: Int,
        result: Result<DataMarkerId>,
    ) {
        val error = assertIs<DataMarkerIdNameError>(result.exceptionOrNull())
        assertEquals(expected, error.expected)
        assertEquals(index, error.index)
    }
}
