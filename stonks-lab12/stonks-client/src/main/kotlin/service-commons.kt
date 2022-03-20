import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryAmount
import javax.money.format.MonetaryFormats

class CurrencyUnitSerializer : JsonSerializer<CurrencyUnit>() {
    override fun serialize(value: CurrencyUnit, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.currencyCode)
    }
}

class CurrencyUnitDeserializer : JsonDeserializer<CurrencyUnit>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CurrencyUnit =
        Monetary.getCurrency(p.codec.readTree<JsonNode>(p).textValue())
}

class MonetaryAmountSerializer : JsonSerializer<MonetaryAmount>() {
    override fun serialize(value: MonetaryAmount, gen: JsonGenerator, serializers: SerializerProvider) {
        val locale = MonetaryFormats.getAvailableLocales().first()
        val format = MonetaryFormats.getAmountFormat(locale)
        gen.writeString(format.format(value))
    }
}

class MonetaryAmountDeserializer : JsonDeserializer<MonetaryAmount>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MonetaryAmount {
        val jsonText = p.codec.readTree<JsonNode>(p).textValue()
        val locale = MonetaryFormats.getAvailableLocales().first()
        val format = MonetaryFormats.getAmountFormat(locale)
        return format.parse(jsonText)
    }
}

private val mapperInitializer: ObjectMapper.() -> Unit = {
    enable(SerializationFeature.INDENT_OUTPUT)
    registerModule(SimpleModule().apply {
        addSerializer(CurrencyUnit::class.java, CurrencyUnitSerializer())
        addDeserializer(CurrencyUnit::class.java, CurrencyUnitDeserializer())
        addSerializer(MonetaryAmount::class.java, MonetaryAmountSerializer())
        addDeserializer(MonetaryAmount::class.java, MonetaryAmountDeserializer())
    })
}

val objectMapper = jacksonObjectMapper().apply(mapperInitializer)

val httpClient = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = JacksonSerializer(objectMapper)
    }

    defaultRequest {
        host = "localhost"
        port = 8080
    }
}

fun HttpRequestBuilder.setJsonBody(data: Any) {
    header(HttpHeaders.ContentType, ContentType.Application.Json)
    body = objectMapper.writeValueAsBytes(data)
}