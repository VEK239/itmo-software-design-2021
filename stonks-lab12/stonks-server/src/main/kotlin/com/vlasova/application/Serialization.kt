package com.vlasova.application

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
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

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            mapperInitializer()
        }
    }
}
