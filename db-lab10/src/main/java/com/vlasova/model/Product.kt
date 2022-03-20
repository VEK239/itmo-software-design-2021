package com.vlasova.model

import org.bson.Document
import com.vlasova.model.Currency.Companion.convert

class Product(private val name: String, private val value: Double, private val currency: Currency) {
    constructor(doc: Document) : this(
        doc.getString("name"),
        doc.getDouble("value"),
        Currency.valueOf(doc.getString("currency"))
    )

    fun asDocument(): Document {
        return Document("name", name).append("value", value).append("currency", currency.toString())
    }

    fun changeCurrency(newCurrency: Currency): Product {
        return Product(name, currency.convert(newCurrency, value), newCurrency)
    }

    fun getValue(need: Currency): Double {
        return currency.convert(need, value)
    }

    override fun toString(): String {
        return "Product(name='$name', value=$value, currency=$currency)"
    }
}