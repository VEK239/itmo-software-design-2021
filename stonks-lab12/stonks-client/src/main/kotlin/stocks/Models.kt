package stocks

import javax.money.MonetaryAmount

const val DEFAULT_CURRENCY = "RUB"

data class Organization(
    val name: String,
    val id: Long = 0
)

data class Stock(
    val price: MonetaryAmount,
    val organization: Organization,
    val id: Long = 0
)
