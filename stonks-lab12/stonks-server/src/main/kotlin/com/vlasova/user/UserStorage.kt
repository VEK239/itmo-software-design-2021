package com.vlasova.user

import org.javamoney.moneta.Money
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.vlasova.stocks.DEFAULT_CURRENCY
import java.math.BigDecimal
import javax.money.MonetaryAmount

class UserStorage {
    suspend fun saveUser(user: User) = newSuspendedTransaction {
        UserEntity.new {
            login = user.login
            balance = Money.of(BigDecimal.ZERO, DEFAULT_CURRENCY)
        }
    }

    suspend fun getUserById(id: Long): User? = newSuspendedTransaction {
        UserEntity.findById(id)?.toUser()
    }

    suspend fun changeUserBalance(userId: Long, delta: MonetaryAmount) = newSuspendedTransaction {
        val user = UserEntity[userId]
        user.apply {
            balance = balance.add(delta)
        }
    }

    suspend fun getUserBalance(userId: Long): MonetaryAmount = newSuspendedTransaction {
        UserEntity[userId].balance
    }
}
