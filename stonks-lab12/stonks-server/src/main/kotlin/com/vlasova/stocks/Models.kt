package com.vlasova.stocks

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.money.compositeMoney
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

object OrganizationTable : LongIdTable("organization") {
    val name = text("name").uniqueIndex()
}

object StockTable : LongIdTable("stock") {
    val organization = reference("organization_id", OrganizationTable, onDelete = ReferenceOption.CASCADE)
    val price = compositeMoney(
        precision = 14,
        scale = 5,
        amountName = "price",
        currencyName = "price_currency"
    )
}

class OrganizationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OrganizationEntity>(OrganizationTable)

    var name by OrganizationTable.name
    val stocks by StockEntity referrersOn StockTable.organization
}

fun OrganizationEntity.toOrganization(): Organization = Organization(
    id = id.value,
    name = name
)

class StockEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object: LongEntityClass<StockEntity>(StockTable)

    val organization by OrganizationEntity referencedOn StockTable.organization
    var price by StockTable.price
}

fun StockEntity.toStock(): Stock = Stock(
    id = id.value,
    organization = organization.toOrganization(),
    price = price
)
