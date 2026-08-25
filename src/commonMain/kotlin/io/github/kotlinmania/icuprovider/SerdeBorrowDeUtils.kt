// port-lint: source serde_borrow_de_utils.rs
package io.github.kotlinmania.icuprovider

import kotlinx.serialization.Serializable

/**
 * Wrapper for string deserialization borrowing.
 */
@Serializable
data class CowWrap(
    val value: String,
)

/**
 * Wrapper for byte array deserialization borrowing.
 */
@Serializable
data class CowBytesWrap(
    val value: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CowBytesWrap) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int = value.contentHashCode()
}

/** Deserializes a list of strings. */
fun arrayOfCow(list: List<CowWrap>): List<String> = list.map { it.value }

/** Deserializes an optional string. */
fun optionOfCow(wrap: CowWrap?): String? = wrap?.value

/** Deserializes a pair of strings. */
fun tupleOfCow(pair: Pair<CowWrap, CowWrap>): Pair<String, String> =
    Pair(pair.first.value, pair.second.value)
