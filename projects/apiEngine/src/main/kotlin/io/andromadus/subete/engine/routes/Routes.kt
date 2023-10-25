package io.andromadus.subete.engine.routes

import io.andromadus.subete.engine.plugins.SECURITY_AUTH_USER
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.apiRoutes() {
    route("/api") {
        authenticate(SECURITY_AUTH_USER) {
            todoRoutes()
        }
    }
}