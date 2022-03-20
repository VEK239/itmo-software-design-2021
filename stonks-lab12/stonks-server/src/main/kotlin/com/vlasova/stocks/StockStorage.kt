package com.vlasova.stocks

import org.javamoney.moneta.Money
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.vlasova.user.UserStocksTable
import com.vlasova.user.UserTable
import java.math.BigDecimal
import javax.money.MonetaryAmount

class StockStorage {
    suspend fun addOrganization(organization: Organization) = newSuspendedTransaction {
        OrganizationEntity.new {
            name = organization.name
        }
    }

    suspend fun getOrganizations(): List<Organization> = newSuspendedTransaction {
        OrganizationEntity.all().map { it.toOrganization() }
    }

    suspend fun getOrganizationById(organizationId: Long): Organization? = newSuspendedTransaction {
        OrganizationEntity.findById(organizationId)?.toOrganization()
    }

    suspend fun addStock(stock: Stock) = newSuspendedTransaction {
        StockTable.insert {
            it[price] = stock.price
            it[organization] = stock.organization.id
        }
    }

    suspend fun getAllStocks(): List<Stock> = newSuspendedTransaction {
        StockEntity.all().map { it.toStock() }
    }

    suspend fun getStockById(stockId: Long): Stock? = newSuspendedTransaction {
        StockEntity.findById(stockId)?.toStock()
    }

    suspend fun getStocksByOrganization(organizationId: Long): List<Stock> = newSuspendedTransaction {
        val organization = OrganizationEntity.findById(organizationId)
        organization?.stocks?.map { it.toStock() } ?: listOf()
    }

    suspend fun addStockToUser(userId: Long, stockId: Long, price: MonetaryAmount) = newSuspendedTransaction {
        UserStocksTable.insert {
            it[UserStocksTable.userId] = userId
            it[UserStocksTable.stockId] = stockId
            it[UserStocksTable.price] = price
        }
    }

    suspend fun deleteStockFromUser(userId: Long, stockId: Long) = newSuspendedTransaction {
        UserStocksTable.deleteWhere {
            (UserStocksTable.userId eq userId) and (UserStocksTable.stockId eq stockId)
        }
    }

    suspend fun getStocksOfUser(userId: Long): List<Stock> = newSuspendedTransaction {
        UserStocksTable.select { UserStocksTable.userId eq userId }.map {
            val stock = getStockById(it[UserStocksTable.stockId].value)!!
            stock.copy(price = it[UserStocksTable.price])
        }
    }

    suspend fun updateStockPrice(stockId: Long, delta: BigDecimal): Stock? = newSuspendedTransaction {
        val stock = StockEntity.findById(stockId) ?: return@newSuspendedTransaction null
        val newPrice = stock.price.add(Money.of(delta, stock.price.currency))
        stock.apply { price = newPrice }
            .toStock()
            .copy(price = newPrice)
    }
}
