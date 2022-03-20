package com.vlasova.application

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.vlasova.stocks.StockUpdaterImpl

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureDatabase()
        configureRouting(::StockUpdaterImpl)
        configureHTTP()
        configureMonitoring()
        configureSerialization()
    }.start(wait = true)
}
