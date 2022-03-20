package com.vlasova.stocks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.vlasova.user.UserStorage
import kotlin.coroutines.CoroutineContext

class StockFacade(
    private val stockStorage: StockStorage,
    private val stockUpdater: StockUpdater,
    private val userStorage: UserStorage
) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    init {
        launch {
            stockUpdater.startUpdating()
        }
    }

    suspend fun addOrganization(organization: Organization) = stockStorage.addOrganization(organization)

    suspend fun getOrganization(organizationId: Long) = stockStorage.getOrganizationById(organizationId)

    suspend fun addStock(organizationId: Long, stock: Stock) = stockStorage.addStock(stock)

    suspend fun getStock(stockId: Long): Stock? = stockStorage.getStockById(stockId)

    suspend fun buyStock(userId: Long, stockId: Long) = newSuspendedTransaction {
        val stock = stockStorage.getStockById(stockId)!!
        userStorage.changeUserBalance(userId, stock.price.negate())
        stockStorage.addStockToUser(userId, stockId, stock.price)
    }

    suspend fun sellStock(userId: Long, stockId: Long) = newSuspendedTransaction {
        val stock = stockStorage.getStockById(stockId)!!
        userStorage.changeUserBalance(userId, stock.price)
        stockStorage.deleteStockFromUser(userId, stockId)
    }
}
