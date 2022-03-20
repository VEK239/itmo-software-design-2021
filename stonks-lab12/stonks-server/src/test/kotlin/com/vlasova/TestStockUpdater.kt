package com.vlasova

import com.vlasova.stocks.StockStorage
import com.vlasova.stocks.StockUpdater

class TestStockUpdater(stockStorage: StockStorage) : StockUpdater(stockStorage) {
    override val updateTimeoutMs = 0L
    override val updateDeltas: DoubleArray = doubleArrayOf(1.0)
}
