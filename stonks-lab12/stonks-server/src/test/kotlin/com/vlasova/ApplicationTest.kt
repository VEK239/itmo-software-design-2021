package com.vlasova

import com.vlasova.stocks.*
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.testcontainers.perTest
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.*
import org.javamoney.moneta.Money
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.GenericContainer
import com.vlasova.user.User
import com.vlasova.user.UserStocksTable
import com.vlasova.user.UserTable
import java.math.BigDecimal
import kotlin.math.exp

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

class ApplicationTest : FreeSpec({
    beforeAny {
        Database.connect(
            url = "jdbc:h2:mem:stonks",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.drop(
                UserTable,
                UserStocksTable,
                StockTable,
                OrganizationTable
            )

            SchemaUtils.create(
                UserTable,
                UserStocksTable,
                StockTable,
                OrganizationTable
            )
        }
    }

    testContext {
        "organization tests" - {
            "add organization" {
                test {
                    addOrganization(testOrganization)

                    handleGetRequest("/api/organization/1") {
                        response.content shouldBe testOrganization.toJson()
                    }
                }
            }
        }

        "stocks tests" - {
            "add stock to organization" {
                test {
                    addOrganization(testOrganization)
                    addStock(testOrganization, testStock)

                    handleGetRequest("/api/organization/1/stocks") {
                        response.content shouldBe listOf(testStock).toJson()
                    }
                }
            }

            "can receive all existing stocks" {
                test {
                    organizations.forEach { addOrganization(it) }
                    stocks.forEach { stock ->
                        addStock(stock.organization, stock)
                    }

                    handleGetRequest("/api/stock") {
                        response.content shouldBe stocks.toJson()
                    }
                }
            }

            "should get stock by id" {
                test {
                    addOrganization(testOrganization)
                    addStock(testOrganization, testStock)

                    handleGetRequest("/api/stock/1") {
                        response.content shouldBe testStock.toJson()
                    }
                }
            }
        }

        "user tests" - {
            "should register user" {
                test {
                    registerUser(testUser)

                    handleGetRequest("/api/user/1") {
                        response.content shouldBe testUser.toJson()
                    }
                }
            }

            "should correctly add balance" {
                test {
                    registerUser(testUser)
                    changeUserBalance(testUser, 20)

                    handleGetRequest("/api/user/1/balance") {
                        response.content shouldBe 20.toMoneyJson()
                    }
                }
            }

            "should correctly take balance" {
                test {
                    registerUser(testUser)
                    changeUserBalance(testUser, 100)
                    changeUserBalance(testUser, -20)

                    handleGetRequest("/api/user/1/balance") {
                        response.content shouldBe 80.toMoneyJson()
                    }
                }
            }
        }

        "user-stocks integration" - {
            fun testWithStocks(block: TestApplicationEngine.() -> Unit) = test {
                registerUser(testUser, balance = userDefaultBalance)
                organizations.forEach { addOrganization(it) }
                stocks.forEach { addStock(it.organization, it) }

                block()
            }

            "buy stock" - {
                "stock should appear in user stock list" {
                    testWithStocks {
                        stocks.forEach { buyStock(testUser, it) }
                        handleGetRequest("/api/user/1/stock") {
                            response.content shouldBe stocks.toJson()
                        }
                    }
                }

                "user balance should decrease" {
                    testWithStocks {
                        buyStock(testUser, testStock)
                        handleGetRequest("/api/user/1/balance") {
                            response.content shouldBe userDefaultBalance.toMoney().subtract(testStock.price).toJson()
                        }
                    }
                }

                "user stock balance should increase" {
                    testWithStocks {
                        buyStock(testUser, testStock)
                        handleGetRequest("/api/user/1/stock/balance") {
                            response.content shouldBe testStock.price.toJson()
                        }
                    }
                }

                "balance with stock should not change without price update" {
                    testWithStocks {
                        buyStock(testUser, testStock)
                        handleGetRequest("/api/user/1/balance?withStocks=true") {
                            response.content shouldBe userDefaultBalance.toMoneyJson()
                        }
                    }
                }
            }

            "sell stock" - {
                "stock should disappear from user stock list" {
                    testWithStocks {
                        stocks.forEach { buyStock(testUser, it) }
                        stocks.forEach { sellStock(testUser, it) }
                        handleGetRequest("/api/user/1/stock") {
                            response.content shouldBe emptyList<Stock>().toJson()
                        }
                    }
                }

                "user balance should increase" {
                    testWithStocks {
                        stocks.forEach { buyStock(testUser, it) }
                        sellStock(testUser, testStock)

                        handleGetRequest("/api/user/1/balance") {
                            var expected = userDefaultBalance.toMoney()
                            stocks.forEach { expected = expected.subtract(it.price) }
                            expected = expected.add(testStock.price)

                            response.content shouldBe expected.toJson()
                        }
                    }
                }

                "user stock balance should decrease" {
                    testWithStocks {
                        stocks.forEach { buyStock(testUser, it) }
                        sellStock(testUser, testStock)

                        handleGetRequest("/api/user/1/stock/balance") {
                            response.content shouldBe stocks.fold(0.toMoney()) { acc, stock ->
                                acc.add(stock.price)
                            }.subtract(testStock.price).toJson()
                        }
                    }
                }

                "balance with stock should not change without price update" {
                    testWithStocks {
                        buyStock(testUser, testStock)
                        sellStock(testUser, testStock)
                        handleGetRequest("/api/user/1/balance?withStocks=true") {
                            response.content shouldBe userDefaultBalance.toMoneyJson()
                        }
                    }
                }
            }
        }
    }
})