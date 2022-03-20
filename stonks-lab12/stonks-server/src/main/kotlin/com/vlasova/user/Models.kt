package com.vlasova.user

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.money.compositeMoney
import com.vlasova.stocks.StockTable
import java.util.*
import javax.money.CurrencyUnit
import javax.money.Monetary
import kotlin.math.log

data class User(
    val login: String,
    val id: Long = 0
)

object UserTable : LongIdTable("user") {
    val login = varchar("login", 60).uniqueIndex()
    val balance = compositeMoney(14, 5, "balance", "balance_currency")
}

class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var login by UserTable.login
    var balance by UserTable.balance

    fun toUser(): User = User(
        login = login,
        id = id.value
    )
}

object UserStocksTable : Table("user_stocks") {
    val userId = reference("user_id", UserTable.id)
    val stockId = reference("stock_id", StockTable.id)
    val price = compositeMoney(14, 5, "price", "price_currency")

    override val primaryKey = PrimaryKey(userId, stockId)
}
