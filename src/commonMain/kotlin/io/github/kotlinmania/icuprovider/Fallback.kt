// port-lint: source fallback.rs
package io.github.kotlinmania.icuprovider

/**
 * Options to define fallback behaviour.
 *
 * These options are consumed by the locale fallbacker in the locales crate, but are defined here
 * because they are used by [DataMarkerInfo].
 */

/**
 * Hint for which subtag to prioritize during fallback.
 *
 * For example, `"en-US"` might fall back to either `"en"` or `"und-US"` depending on this enum.
 */
enum class LocaleFallbackPriority {
    /**
     * Prioritize the language. This is the default behavior.
     *
     * For example, `"en-US"` should go to `"en"` and then `"und"`.
     */
    Language,

    /**
     * Prioritize the script.
     *
     * For example, `"en-US"` should go to `"en"` and then `"und-Latn"` and then `"und"`.
     */
    Script,

    /**
     * Prioritize the region.
     *
     * For example, `"en-US"` should go to `"und-US"` and then `"und"`.
     */
    Region,
    ;

    companion object {
        /** Const-friendly version of the default. */
        fun default(): LocaleFallbackPriority = Language
    }
}

/** Configuration settings for a particular fallback operation. */
data class LocaleFallbackConfig(
    /** Strategy for choosing which subtags to drop during locale fallback. */
    val priority: LocaleFallbackPriority = LocaleFallbackPriority.default(),
) {
    companion object {
        /** Const version of the default. */
        fun default(): LocaleFallbackConfig = LocaleFallbackConfig()
    }
}
