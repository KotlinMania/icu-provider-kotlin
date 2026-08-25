// port-lint: source lib.rs
package io.github.kotlinmania.icuprovider

import kotlin.test.Test
import kotlin.test.assertNotNull

class LibTest {
    @Test
    fun testLogging() {
        assertNotNull(Lib)
    }
}
