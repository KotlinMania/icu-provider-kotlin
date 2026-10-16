// port-lint: source hello_world.rs
package io.github.kotlinmania.icuprovider

import io.github.kotlinmania.iculocalecore.DataLocale
import io.github.kotlinmania.iculocalecore.LanguageIdentifier
import io.github.kotlinmania.iculocalecore.preferences.LocalePreferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HelloWorldTest {
    @Test
    fun testIter() {
        val provider = HelloWorldProvider()
        val ids = provider.iterIds().getOrThrow()
        assertTrue(ids.isNotEmpty())
        assertTrue(ids.any { it.locale.toString() == "en" })
        assertTrue(ids.any { it.locale.toString() == "de" })
    }

    @Test
    fun testHelloWorldFormatter() {
        val prefs =
            LocalePreferences.from(
                LanguageIdentifier.parse("de").getOrThrow(),
            )
        val formatter =
            HelloWorldFormatter
                .tryNew(
                    HelloWorldFormatterPreferences(prefs),
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
                    DataRequest(id = DataIdentifierBorrowed.forLocale(DataLocale.parse("de").getOrThrow())),
                ).getOrThrow()
        assertTrue(response.payload.get().isNotEmpty())
    }
}
