package user

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import setJsonBody
import stocks.Stock
import java.math.BigDecimal
import javax.money.MonetaryAmount

class UserService(private val httpClient: HttpClient) {
    suspend fun registerUser(user: User) {
        httpClient.post<HttpResponse>("/api/user/register") {
            setJsonBody(user)
        }
    }

    suspend fun getUser(userId: Long): User =
        httpClient.get("/api/user/$userId")

    suspend fun getUserStocks(user: User): List<Stock> =
        httpClient.get("/api/user/${user.id}/stock")

    suspend fun buyStock(user: User, stock: Stock) {
        httpClient.post<HttpResponse>("/api/user/${user.id}/stock/buy") {
            parameter("stockId", stock.id)
        }
    }

    suspend fun sellStock(user: User, stock: Stock) {
        httpClient.post<HttpResponse>("/api/user/${user.id}/stock/sell") {
            parameter("stockId", stock.id)
        }
    }

    suspend fun getUserStockBalance(user: User): MonetaryAmount =
        httpClient.get("/api/user/${user.id}/stock/balance")

    suspend fun getUserBalance(user: User, withStock: Boolean = false): MonetaryAmount =
        httpClient.get("/api/user/${user.id}/balance") {
            parameter("withStock", withStock)
        }

    suspend fun changeUserBalance(user: User, delta: Long) = changeUserBalance(user, delta.toBigDecimal())

    private suspend fun changeUserBalance(user: User, delta: BigDecimal) {
        httpClient.post<HttpResponse>("/api/user/${user.id}/balance") {
            parameter("delta", delta.toPlainString())
        }
    }
}