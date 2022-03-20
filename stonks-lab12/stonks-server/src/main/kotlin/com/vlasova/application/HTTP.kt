package com.vlasova.application

import io.ktor.http.*
import io.ktor.application.*
import io.ktor.features.*

fun Application.configureHTTP() {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.AccessControlAllowHeaders)
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()
    }
}
