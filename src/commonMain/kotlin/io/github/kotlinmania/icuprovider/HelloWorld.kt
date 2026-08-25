// port-lint: source hello_world.rs
package io.github.kotlinmania.icuprovider

import io.github.kotlinmania.icuprovider.buf.BufferFormat
import io.github.kotlinmania.icuprovider.buf.BufferMarker
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** A struct containing "Hello World" in the requested language. */
@Serializable
data class HelloWorld(
    /** The translation of "Hello World". */
    val message: String = "(und) Hello World",
) {
    companion object {
        fun default(): HelloWorld = HelloWorld()

        fun zeroFrom(message: String): HelloWorld = HelloWorld(message)
    }
}

/** Marker type for [HelloWorld]. */
object HelloWorldV1 : DataMarker<HelloWorld> {
    override val info: DataMarkerInfo =
        dataMarker<HelloWorld>(
            name = "HelloWorldV1",
            hasChecksum = true,
        )

    val INFO: DataMarkerInfo = info
}

/**
 * A data provider returning Hello World strings in different languages.
 *
 * Mostly useful for testing.
 */
class HelloWorldProvider :
    DataProvider<HelloWorldV1, HelloWorld>,
    DryDataProvider<HelloWorldV1, HelloWorld>,
    IterableDataProvider<HelloWorldV1, HelloWorld> {
    override fun load(request: DataRequest): Result<DataResponse<HelloWorldV1, HelloWorld>> {
        val entry =
            DATA.find { (localeStr, attrStr, _) ->
                request.id.locale.value == localeStr && attrStr == request.id.markerAttributes.asString()
            } ?: return Result.failure(DataErrorKind.IdentifierNotFound.withReq(HelloWorldV1.INFO, request))

        return Result.success(
            DataResponse(
                metadata = DataResponseMetadata.default().withChecksum(1234uL),
                payload = DataPayload.fromStaticStr(entry.third),
            ),
        )
    }

    override fun dryLoad(request: DataRequest): Result<DataResponseMetadata> =
        load(request).map { it.metadata }

    override fun iterIds(): Result<Set<DataIdentifierCow>> =
        Result.success(
            DATA
                .map { (localeStr, attrStr, _) ->
                    DataIdentifierCow.fromBorrowedAndOwned(
                        DataMarkerAttributes.fromStringOrPanic(attrStr),
                        DataLocale(localeStr),
                    )
                }.toSet(),
        )

    /** Converts this provider into a buffer provider that uses JSON serialization. */
    fun intoJsonProvider(): HelloWorldJsonProvider = HelloWorldJsonProvider(this)

    override fun equals(other: Any?): Boolean = other is HelloWorldProvider

    override fun hashCode(): Int = HelloWorldProvider::class.hashCode()

    companion object {
        val DATA: List<Triple<String, String, String>> =
            listOf(
                Triple("bn", "", "ওহে বিশ্ব"),
                Triple("cs", "", "Ahoj světe"),
                Triple("de", "", "Hallo Welt"),
                Triple("de-AT", "", "Servus Welt"),
                Triple("el", "", "Καλημέρα κόσμε"),
                Triple("en", "", "Hello World"),
                // WORLD
                Triple("en-001", "", "Hello from 🗺️"),
                // AFRICA
                Triple("en-002", "", "Hello from 🌍"),
                // AMERICAS
                Triple("en-019", "", "Hello from 🌎"),
                // ASIA
                Triple("en-142", "", "Hello from 🌏"),
                // GREAT BRITAIN
                Triple("en-GB", "", "Hello from 🇬🇧"),
                // ENGLAND
                Triple("en-GB-u-sd-gbeng", "", "Hello from 🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
                Triple("en", "reverse", "Olleh Dlrow"),
                Triple("eo", "", "Saluton, Mondo"),
                Triple("fa", "", "سلام دنیا‎"),
                Triple("fi", "", "hei maailma"),
                Triple("is", "", "Halló, heimur"),
                Triple("ja", "", "こんにちは世界"),
                Triple("ja", "reverse", "界世はちにんこ"),
                Triple("la", "", "Ave, munde"),
                Triple("pt", "", "Olá, mundo"),
                Triple("ro", "", "Salut, lume"),
                Triple("ru", "", "Привет, мир"),
                Triple("sr", "", "Поздрав свете"),
                Triple("sr-Latn", "", "Pozdrav svete"),
                Triple("vi", "", "Xin chào thế giới"),
                Triple("zh", "", "你好世界"),
            )
    }
}

/**
 * A data provider returning Hello World strings in different languages as JSON blobs.
 *
 * Mostly useful for testing.
 */
class HelloWorldJsonProvider(
    private val underlying: HelloWorldProvider = HelloWorldProvider(),
) : DynamicDataProvider<BufferMarker, ByteArray> {
    override fun loadData(
        marker: DataMarkerInfo,
        request: DataRequest,
    ): Result<DataResponse<BufferMarker, ByteArray>> {
        marker.matchMarker(HelloWorldV1.INFO).getOrElse { return Result.failure(it) }
        val result = underlying.load(request).getOrElse { return Result.failure(it) }
        val jsonStr = Json.encodeToString(result.payload.get())
        return Result.success(
            DataResponse(
                metadata =
                    DataResponseMetadata(
                        bufferFormat = BufferFormat.Json.name,
                        locale = result.metadata.locale,
                        checksum = result.metadata.checksum,
                    ),
                payload = DataPayload.fromOwnedBuffer(jsonStr.encodeToByteArray()),
            ),
        )
    }
}

/** Hello World Preferences. */
data class HelloWorldFormatterPreferences(
    val localePreferences: LocalePreferences = LocalePreferences(),
)

/**
 * A type that formats localized "hello world" strings.
 *
 * This type is intended to take the shape of a typical ICU4X formatter API.
 */
class HelloWorldFormatter internal constructor(
    val data: DataPayload<HelloWorldV1, HelloWorld>,
) {
    /** Formats a hello world message, returning a [FormattedHelloWorld]. */
    fun format(): FormattedHelloWorld = FormattedHelloWorld(data.get())

    /** Formats a hello world message, returning a string. */
    fun formatToString(): String = format().toString()

    companion object {
        /** Creates a new [HelloWorldFormatter] for the specified locale preferences. */
        fun tryNew(prefs: HelloWorldFormatterPreferences = HelloWorldFormatterPreferences()): Result<HelloWorldFormatter> =
            tryNewUnstable(HelloWorldProvider(), prefs)

        /** Creates a new [HelloWorldFormatter] using custom data from a [DataProvider]. */
        fun tryNewUnstable(
            provider: DataProvider<HelloWorldV1, HelloWorld>,
            prefs: HelloWorldFormatterPreferences = HelloWorldFormatterPreferences(),
        ): Result<HelloWorldFormatter> {
            val locale = HelloWorldV1.makeLocale(prefs.localePreferences)
            val response =
                provider
                    .load(
                        DataRequest(
                            id = DataIdentifierBorrowed.forLocale(locale),
                        ),
                    ).getOrElse { return Result.failure(it) }
            return Result.success(HelloWorldFormatter(response.payload))
        }
    }
}

/** A formatted hello world message. */
data class FormattedHelloWorld(
    val data: HelloWorld,
) {
    override fun toString(): String = data.message
}
