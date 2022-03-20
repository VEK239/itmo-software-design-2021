package com.vlasova

import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.javamoney.moneta.Money
import com.vlasova.application.configureMonitoring
import com.vlasova.application.configureRouting
import com.vlasova.application.configureSerialization
import com.vlasova.application.objectMapper
import com.vlasova.stocks.*
import com.vlasova.user.User
import java.math.BigDecimal
import java.math.RoundingMode
import javax.money.MonetaryAmount

fun testContext(block: TestContext.() -> Unit) {
    TestContext(::TestStockUpdater).run(block)
}

class TestContext(private val testStockUpdater: (StockStorage) -> StockUpdater) {
    fun test(block: TestApplicationEngine.() -> Unit) {
        val initApplication: Application.() -> Unit = {
            configureRouting(testStockUpdater)
            configureMonitoring()
            configureSerialization()
        }

        withTestApplication(initApplication) {
            block()
        }
    }

    fun TestApplicationEngine.handleGetRequest(
        uri: String,
        setup: TestApplicationRequest.() -> Unit = {},
        afterCallBlock: TestApplicationCall.() -> Unit
    ): TestApplicationCall = handleRequest(HttpMethod.Get, uri, setup).apply(afterCallBlock)


    fun TestApplicationEngine.addOrganization(organization: Organization) {
        handleRequest(HttpMethod.Post, "/api/organization") {
            setJsonBody(organization)
        }.apply { response.status() shouldBe HttpStatusCode.OK }
    }

    fun TestApplicationEngine.addStock(organization: Organization, stock: Stock) {
        handleRequest(HttpMethod.Post, "/api/organization/${organization.id}/stocks") {
            setJsonBody(stock)
        }.apply { response.status() shouldBe HttpStatusCode.OK }
    }

    fun TestApplicationEngine.registerUser(user: User, balance: Long = 0) {
        handleRequest(HttpMethod.Post, "/api/user/register") {
            setJsonBody(user)
        }.apply { response.status() shouldBe HttpStatusCode.OK }

        if (balance != 0L) {
            changeUserBalance(user, balance)
        }
    }

    fun TestApplicationEngine.changeUserBalance(user: User, delta: Double) {
        val deltaBigDecimal = BigDecimal(delta).setScale(2, RoundingMode.HALF_EVEN)
        val url = "/api/user/${user.id}/balance?delta=${deltaBigDecimal.toPlainString()}"
        handleRequest(HttpMethod.Post, url).apply {
            response.status() shouldBe HttpStatusCode.OK
        }
    }

    fun TestApplicationEngine.changeUserBalance(user: User, delta: Long) =
        changeUserBalance(user, delta.toDouble())

    fun TestApplicationEngine.buyStock(user: User, stock: Stock) {
        handleRequest(HttpMethod.Post, "/api/user/${user.id}/stock/buy?stockId=${stock.id}").apply {
            response.status() shouldBe HttpStatusCode.OK
        }
    }

    fun TestApplicationEngine.sellStock(user: User, stock: Stock) {
        handleRequest(HttpMethod.Post, "/api/user/${user.id}/stock/sell?stockId=${stock.id}").apply {
            response.status() shouldBe HttpStatusCode.OK
        }
    }


    private fun TestApplicationRequest.setContentBodyJson() {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }

    private fun TestApplicationRequest.setJsonBody(data: Any) {
        setContentBodyJson()
        setBody(objectMapper.writeValueAsBytes(data))
    }
}

fun Any.toJson(): String = objectMapper.writeValueAsString(this)
fun Number.toMoneyJson(): String = objectMapper.writeValueAsString(this.toMoney())
fun Number.toMoney(): MonetaryAmount = Money.of(this, DEFAULT_CURRENCY)