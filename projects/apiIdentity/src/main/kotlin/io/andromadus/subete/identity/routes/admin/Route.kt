package io.andromadus.subete.identity.routes.admin

import io.ktor.server.routing.*

fun Route.adminModuleRoutes() {
    route("/admin") {
        adminModuleUserRoutes()
    }
}