package com.vlasova

import httpClient
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.*
import org.javamoney.moneta.Money
import org.testcontainers.containers.GenericContainer
import io.kotest.extensions.testcontainers.perTest
import org.testcontainers.containers.InternetProtocol
import stocks.DEFAULT_CURRENCY
import stocks.Organization
import stocks.Stock
import stocks.StockService
import user.User
import user.UserService
import java.math.BigDecimal

private val organizations = listOf(
    Organization("org-1", 1),
    Organization("org-2", 2),
)

private val testOrganization
    get() = organizations.first()

private val stocks = listOf(
    Stock(
        price = Money.of(BigDecimal.TEN, DEFAULT_CURRENCY),
        organization = testOrganization,
        id = 1
    ),
    Stock(
        price = Money.of(BigDecimal.ONE, DEFAULT_CURRENCY),
        organization = organizations[1],
        id = 2
    )
)

private val testStock
    get() = stocks.first()

private val testUser = User("test", 1)
private const val userDefaultBalance = 1000L

class StocksTest : FreeSpec({
    class TestContainer : GenericContainer<TestContainer>("stonks:0.0.1") {
        init {
            addFixedExposedPort(8080, 8080, InternetProtocol.TCP)
            withExposedPorts(8080)
        }
    }

    val container = TestContainer()
    listener(container.perTest())

    val stockService = StockService(httpClient)
    val userService = UserService(httpClient)

    with(stockService) {
        with(userService) {
            "organization tests" - {
                "add organization" {
                    addOrganization(testOrganization)
                    getOrganization(testOrganization.id) shouldBe testOrganization
                }
            }

            "stocks tests" - {
                "add stock to organization" {
                    addOrganization(testOrganization)
                    addStock(testOrganization, testStock)
                    getOrganizationStocks(testOrganization.id) shouldBe listOf(testStock)
                }

                "can receive all existing stocks" {
                    organizations.forEach { addOrganization(it) }
                    stocks.forEach { stock ->
                        addStock(stock.organization, stock)
                    }
                    getAllStocks() shouldBe stocks
                }

                "should get stock by id" {
                    addOrganization(testOrganization)
                    addStock(testOrganization, testStock)
                    getStock(testStock.id) shouldBe testStock
                }

            }

            "user tests" - {
                "should register user" {
                    registerUser(testUser)
                    getUser(testUser.id) shouldBe testUser
                }

                "should correctly add balance" {
                    registerUser(testUser)
                    changeUserBalance(testUser, 20)
                    getUserBalance(testUser) shouldBe 20.toMoney()
                }

                "should correctly take balance" {
                    registerUser(testUser)
                    changeUserBalance(testUser, 100)
                    changeUserBalance(testUser, -20)
                    getUserBalance(testUser) shouldBe 80.toMoney()
                }
            }

            "user-stocks integration" - {
                suspend fun testWithStocks(block: suspend () -> Unit) {
                    registerUser(testUser)
                    changeUserBalance(testUser, userDefaultBalance)
                    organizations.forEach { addOrganization(it) }
                    stocks.forEach { addStock(it.organization, it) }

                    block()
                }

                "buy stock" - {
                    "stock should appear in user stock list" {
                        testWithStocks {
                            stocks.forEach { buyStock(testUser, it) }
                            getUserStocks(testUser) shouldBe stocks
                        }
                    }

                    "user balance should decrease" {
                        testWithStocks {
                            buyStock(testUser, testStock)

                            val expected = userDefaultBalance.toMoney().subtract(testStock.price)
                            getUserBalance(testUser) shouldBe expected
                        }
                    }

                    "user stock balance should increase" {
                        testWithStocks {
                            buyStock(testUser, testStock)
                            getUserBalance(testUser) shouldBe testStock.price
                        }
                    }

                    "balance with stock should not change without price update" {
                        testWithStocks {
                            buyStock(testUser, testStock)
                            getUserBalance(testUser, withStock = true) shouldBe userDefaultBalance
                        }
                    }
                }

                "sell stock" - {
                    "stock should disappear from user stock list" {
                        testWithStocks {
                            stocks.forEach { buyStock(testUser, it) }
                            stocks.forEach { sellStock(testUser, it) }
                            getUserStocks(testUser) shouldBe emptyList()
                        }
                    }

                    "user balance should increase" {
                        testWithStocks {
                            stocks.forEach { buyStock(testUser, it) }
                            sellStock(testUser, testStock)

                            var expected = userDefaultBalance.toMoney()
                            stocks.forEach { expected = expected.subtract(it.price) }
                            expected = expected.add(testStock.price)

                            getUserBalance(testUser) shouldBe expected
                        }
                    }

                    "user stock balance should decrease" {
                        testWithStocks {
                            stocks.forEach { buyStock(testUser, it) }
                            sellStock(testUser, testStock)

                            val expected = stocks.fold(0.toMoney()) { acc, stock ->
                                acc.add(stock.price)
                            }.subtract(testStock.price)

                            getUserStockBalance(testUser) shouldBe expected
                        }
                    }

                    "balance with stock should not change without price update" {
                        testWithStocks {
                            buyStock(testUser, testStock)
                            sellStock(testUser, testStock)
                            getUserBalance(testUser, withStock = true) shouldBe userDefaultBalance
                        }
                    }
                }
            }
        }
    }
})