package com.vlasova.application

import com.vlasova.stocks.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.javamoney.moneta.Money
import com.vlasova.user.User
import com.vlasova.user.UserFacade
import com.vlasova.user.UserStorage

fun Application.configureRouting(stockUpdaterProvider: (StockStorage) -> StockUpdater) {
    val stockStorage = StockStorage()
    val userStorage = UserStorage()

    val userFacade = UserFacade(userStorage, stockStorage)
    val stockFacade = StockFacade(
        stockStorage = stockStorage,
        stockUpdater = stockUpdaterProvider(stockStorage),
        userStorage = userStorage
    )

    routing {
        static("/static") {
            resources("static")
        }

        route("/api") {
            route("/user") {
                post("/register") {
                    val user = call.receive<User>()
                    userFacade.registerUser(user)
                    call.respondOk()
                }

                route("/{id}") {
                    get("") {
                        val userId = call.receiveId()
                        val user = userFacade.getUserById(userId)
                        call.respondOk(user)
                    }

                    route("/stock") {
                        get("") {
                            val userId = call.receiveId()
                            call.respondOk(stockStorage.getStocksOfUser(userId))
                        }

                        post("/buy") {
                            val userId = call.receiveId()
                            val stockId = call.request.queryParameters.getOrFail("stockId").toLong()
                            stockFacade.buyStock(userId, stockId)
                            call.respondOk()
                        }

                        post("/sell") {
                            val userId = call.receiveId()
                            val stockId = call.request.queryParameters.getOrFail("stockId").toLong()
                            stockFacade.sellStock(userId, stockId)
                            call.respondOk()
                        }

                        get("/balance") {
                            val userId = call.receiveId()
                            call.respondOk(userFacade.getStocksPrice(userId))
                        }
                    }

                    get("/balance") {
                        val userId = call.receiveId()
                        val withStocks = call.request.queryParameters["withStocks"]?.toBoolean() ?: false
                        val balance = userFacade.getBalance(userId)
                        when (withStocks) {
                            true -> balance.add(userFacade.getStocksPrice(userId))
                            false -> balance
                        }.let { call.respondOk(it) }
                    }

                    post("/balance") {
                        val userId = call.receiveId()
                        val delta = call.request.queryParameters.getOrFail("delta").toBigDecimal()
                        userFacade.changeBalance(userId, Money.of(delta, DEFAULT_CURRENCY))
                        call.respondOk()
                    }
                }
            }

            route("/organization") {
                post("") {
                    val organization = call.receive<Organization>()
                    stockFacade.addOrganization(organization)
                    call.respondOk()
                }

                route("/{id}") {
                    get("") {
                        val id = call.receiveId()
                        call.respondOk(stockFacade.getOrganization(id))
                    }

                    get("/stocks") {
                        val id = call.receiveId()
                        call.respondOk(stockStorage.getStocksByOrganization(id))
                    }

                    post("/stocks") {
                        val stock = call.receive<Stock>()
                        stockFacade.addStock(call.receiveId(), stock)
                        call.respondOk()
                    }
                }
            }

            route("/stock") {
                get("") {
                    call.respondOk(stockStorage.getAllStocks())
                }

                get("/{id}") {
                    val id = call.receiveId()
                    call.respondOk(stockStorage.getStockById(id))
                }
            }
        }
    }
}

fun ApplicationCall.receiveId(): Long = parameters.getOrFail("id").toLong()

suspend inline fun ApplicationCall.respondOk() = respond(HttpStatusCode.OK)
suspend inline fun <reified T : Any> ApplicationCall.respondOk(message: T?) = when (message) {
    null -> respond(HttpStatusCode.NotFound, "")
    else -> respond(HttpStatusCode.OK, message)
}
