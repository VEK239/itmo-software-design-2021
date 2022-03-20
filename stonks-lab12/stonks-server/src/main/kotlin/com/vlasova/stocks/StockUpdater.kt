package com.vlasova.stocks

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class StockUpdater(private val stockStorage: StockStorage) {
    abstract val updateTimeoutMs: Long
    abstract val updateDeltas: DoubleArray

    suspend fun updateStockPrices() =
        newSuspendedTransaction {
            stockStorage.getOrganizations().forEach { organization ->
                stockStorage.getStocksByOrganization(organization.id).forEach { stock ->
                    stockStorage.updateStockPrice(
                        stock.id,
                        updateDeltas.random().toBigDecimal()
                    )
                }
            }
        }

    suspend fun startUpdating() = withContext(Dispatchers.IO) {
        if (updateTimeoutMs > 0L) {
            while (true) {
                updateStockPrices()
                delay(updateTimeoutMs)
            }
        }
    }
}

class StockUpdaterImpl(stockStorage: StockStorage) : StockUpdater(stockStorage) {
    override val updateTimeoutMs = STOCK_UPDATE_TIMEOUT_MS
    override val updateDeltas = (0..1000).map { it / 100.0 }.toDoubleArray()

    companion object {
        private const val STOCK_UPDATE_TIMEOUT_MS = 3000L
    }
}
