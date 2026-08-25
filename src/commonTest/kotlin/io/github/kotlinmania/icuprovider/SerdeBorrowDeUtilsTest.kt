// port-lint: source serde_borrow_de_utils.rs
package io.github.kotlinmania.icuprovider

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class DemoOption(
    val value: CowWrap? = null,
)

@Serializable
data class DemoTuple(
    val first: CowWrap,
    val second: CowWrap,
)

@Serializable
data class DemoArray(
    val items: List<CowWrap>,
)

class SerdeBorrowDeUtilsTest {
    @Test
    fun testOption() {
        val orig = DemoOption(CowWrap("Hello world"))
        val json = Json.encodeToString(orig)
        val decoded = Json.decodeFromString<DemoOption>(json)
        assertEquals(orig, decoded)
        assertEquals("Hello world", optionOfCow(decoded.value))
    }

    @Test
    fun testTuple() {
        val orig = DemoTuple(CowWrap("Hello world"), CowWrap("Hello earth"))
        val json = Json.encodeToString(orig)
        val decoded = Json.decodeFromString<DemoTuple>(json)
        assertEquals(orig, decoded)
        val tuple = tupleOfCow(Pair(decoded.first, decoded.second))
        assertEquals(Pair("Hello world", "Hello earth"), tuple)
    }

    @Test
    fun testArray() {
        val orig = DemoArray(listOf(CowWrap("Hello world")))
        val json = Json.encodeToString(orig)
        val decoded = Json.decodeFromString<DemoArray>(json)
        assertEquals(orig, decoded)
        assertEquals(listOf("Hello world"), arrayOfCow(decoded.items))
    }
}
