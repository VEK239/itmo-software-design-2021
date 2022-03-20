package com.vlasova.application

import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.vlasova.stocks.OrganizationTable
import com.vlasova.stocks.StockTable
import com.vlasova.user.UserStocksTable
import com.vlasova.user.UserTable

fun Application.configureDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:stonks;DB_CLOSE_DELAY=-1;",
        driver = "org.h2.Driver"
    )

    transaction {
        SchemaUtils.create(
            OrganizationTable,
            UserTable,
            StockTable,
            UserStocksTable
        )
    }
}
