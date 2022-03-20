package com.vlasova

import objectMapper
import org.javamoney.moneta.Money
import stocks.DEFAULT_CURRENCY
import javax.money.MonetaryAmount

fun Any.toJson(): String = objectMapper.writeValueAsString(this)
fun Number.toMoneyJson(): String = objectMapper.writeValueAsString(this.toMoney())
fun Number.toMoney(): MonetaryAmount = Money.of(this, DEFAULT_CURRENCY)