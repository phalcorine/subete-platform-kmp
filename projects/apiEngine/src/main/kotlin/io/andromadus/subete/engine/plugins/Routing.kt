package io.andromadus.subete.engine.plugins

import io.andromadus.subete.api.ApiErrorResponse
import io.andromadus.subete.engine.routes.apiRoutes
import io.andromadus.subete.jvm.api.Messages
import io.andromadus.subete.jvm.infra.ktor.KtorAuthorizationException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(StatusPages) {
        exception<KtorAuthorizationException> { call, ktorAuthorizationException ->
            call.respond(
                status = HttpStatusCode.Unauthorized,
                ApiErrorResponse(ktorAuthorizationException.message ?: Messages.USER_NOT_AUTHENTICATED)
            )
        }
    }

    routing {
        get("/") {
            call.respondText("Welcome to Subete (Todo) - Engine")
        }

        apiRoutes()

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}