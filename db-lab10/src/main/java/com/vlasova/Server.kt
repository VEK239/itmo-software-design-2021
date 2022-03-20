package com.vlasova

import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.netty.protocol.http.server.HttpServer
import com.vlasova.db.ReactiveMongoDriver
import com.vlasova.db.ReactiveMongoDriver.Companion.MONGO_DB_URL
import com.vlasova.model.Currency
import com.vlasova.model.Product
import com.vlasova.model.User
import rx.Observable

typealias Action = String

object Server {
    private var driver = ReactiveMongoDriver(MONGO_DB_URL)

    @JvmStatic
    fun main(args: Array<String>) {
        HttpServer
                .newServer(8080)
                .start { request, response ->
                    request.decodedPath.substring(request.decodedPath.lastIndexOf("/") + 1).let { action ->
                        response.writeString(run {
                            var responseMessage = Observable.just("")

                            runCatching {
                                responseMessage = action.handleMapping(request.queryParameters)
                            }.onFailure {
                                responseMessage = Observable.just(it.message)
                                response.status = HttpResponseStatus.BAD_REQUEST
                            }
                            responseMessage
                        })
                    }
                }
                .awaitShutdown()
    }

    private fun Action.handleMapping(queryParameters: Map<String, List<String>>): Observable<String> {
        return when (this) {
            "register" -> Server::handleRegistration
            "add-product" -> Server::handleAddProduct
            "product" -> Server::handleGetProducts
            else -> throw RuntimeException("Incorrect command")
        }(queryParameters)
    }

    private fun handleGetProducts(queryParameters: Map<String, List<String>>) =
            (queryParameters["id"] ?: error("")).first().toInt().let { id ->
                driver.getUser(id).map(User::currency)
                        .flatMap { currency: Currency ->
                            driver.allProducts
                                    .map { "${it.changeCurrency(currency)}\n" }
                        }
            }

    private fun handleAddProduct(queryParameters: Map<String, List<String>>) =
            ((queryParameters["name"] ?: error("")).first()
                    to (queryParameters["value"] ?: error("")).first().toDouble()).let { (name, value) ->
                Currency.valueOf((queryParameters["currency"] ?: error("")).first()).let { currency ->
                    driver.addProduct(Product(name, value, currency)).map { "Product $name inserted: code $it" }
                }
            }

    private fun handleRegistration(queryParameters: Map<String, List<String>>) =
            ((queryParameters["id"] ?: error("")).first().toInt()
                    to Currency.valueOf((queryParameters["currency"] ?: error("")).first())).let { (id, currency) ->
                driver.addUser(User(id, currency)).map { "User $id inserted: code $it" }
            }
}
