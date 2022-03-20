package com.vlasova.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.javamoney.moneta.Money
import com.vlasova.stocks.DEFAULT_CURRENCY
import com.vlasova.stocks.StockStorage
import java.math.BigDecimal
import javax.money.MonetaryAmount

class UserFacade(
    private val userStorage: UserStorage,
    private val stockStorage: StockStorage
) {
    suspend fun registerUser(user: User): Unit = withContext(Dispatchers.IO) {
        userStorage.saveUser(user)
    }

    suspend fun getUserById(userId: Long): User? = withContext(Dispatchers.IO) {
        userStorage.getUserById(userId)
    }

    suspend fun getStocksPrice(userId: Long): MonetaryAmount = withContext(Dispatchers.IO) {
        val stocks = stockStorage.getStocksOfUser(userId)
        stocks.fold(Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY)) { acc, stock ->
            acc.add(stock.price)
        }
    }

    suspend fun getBalance(userId: Long): MonetaryAmount = withContext(Dispatchers.IO) {
        userStorage.getUserBalance(userId)
    }

    suspend fun changeBalance(userId: Long, delta: MonetaryAmount) = withContext(Dispatchers.IO) {
        userStorage.changeUserBalance(userId, delta)
    }
}
