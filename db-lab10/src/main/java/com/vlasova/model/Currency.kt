package com.vlasova.model

enum class Currency(private val cost: Double) {
    RUB(1.0),
    EUR(0.0084),
    USD(0.0093);

    companion object {
        fun Currency.convert(to: Currency, value: Double): Double {
          return value * to.cost / cost
        }
    }
}