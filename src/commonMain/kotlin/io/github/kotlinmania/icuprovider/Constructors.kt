// port-lint: source constructors.rs
package io.github.kotlinmania.icuprovider

/**
 * Documentation and conventions for ICU4X constructor signatures.
 *
 * In ICU4X, data can always be explicitly passed to any function that requires data.
 *
 * Subsequently, there are 3 versions of ICU4X functions that use data:
 * 1. `tryNew` (default compiled / baked data)
 * 2. `tryNewUnstable` (using a [DataProvider])
 * 3. `tryNewWithBufferProvider` (using a [io.github.kotlinmania.icuprovider.buf.BufferProvider])
 */
object Constructors
