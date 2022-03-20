package com.vlasova.model

import org.bson.Document

class User(private val id: Int, val currency: Currency) {
    constructor(document: Document) : this(document.getInteger("id"), Currency.valueOf(document.getString("currency")))

    fun asDocument(): Document {
        return Document("id", id).append("currency", currency.toString())
    }
}