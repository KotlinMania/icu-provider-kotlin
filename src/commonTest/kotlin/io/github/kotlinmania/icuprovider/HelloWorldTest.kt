// port-lint: source hello_world.rs
package io.github.kotlinmania.icuprovider

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HelloWorldTest {
    @Test
    fun testIter() {
        val provider = HelloWorldProvider()
        val ids = provider.iterIds().getOrThrow()
        assertTrue(ids.isNotEmpty())
        assertTrue(ids.any { it.locale.value == "en" })
        assertTrue(ids.any { it.locale.value == "de" })
    }

    @Test
    fun testHelloWorldFormatter() {
        val formatter =
            HelloWorldFormatter
                .tryNew(
                    HelloWorldFormatterPreferences(LocalePreferences(locale = DataLocale("de"))),
                ).getOrThrow()
        assertEquals("Hallo Welt", formatter.formatToString())
    }

    @Test
    fun testHelloWorldJsonProvider() {
        val provider = HelloWorldProvider()
        val jsonProvider = provider.intoJsonProvider()
        val response =
            jsonProvider
                .loadData(
                    HelloWorldV1.INFO,
                    DataRequest(id = DataIdentifierBorrowed.forLocale(DataLocale("de"))),
                ).getOrThrow()
        assertTrue(response.payload.get().isNotEmpty())
    }
}
