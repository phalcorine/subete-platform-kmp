package io.andromadus.subete.identity.routes

import io.andromadus.subete.identity.routes.admin.adminModuleRoutes
import io.ktor.server.routing.*

fun Route.apiRoutes() {
    route("/api") {
        adminModuleRoutes()
        authRoutes()
    }
}