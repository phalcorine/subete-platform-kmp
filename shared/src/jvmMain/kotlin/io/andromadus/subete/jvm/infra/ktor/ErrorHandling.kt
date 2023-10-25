package io.andromadus.subete.jvm.infra.ktor

import io.andromadus.subete.jvm.api.*
import io.andromadus.subete.jvm.api.UnknownError
import io.andromadus.subete.api.ApiErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*


// ApiError Response Handler
suspend fun PipelineContext<Unit, ApplicationCall>.handleError(apiError: ApiError) {
    val httpError = when (apiError) {
        is NotAuthenticatedError -> HttpStatusCode.Unauthorized
        is BadRequestError -> HttpStatusCode.BadRequest
        is ConflictError -> HttpStatusCode.Conflict
        is NotFoundError -> HttpStatusCode.NotFound
        is InternalServerError, is UnknownError -> HttpStatusCode.InternalServerError
    }

    call.respond(
        status = httpError,
        ApiErrorResponse(message = apiError.message)
    )
}

fun HttpStatusCode.toApiError(data: ApiErrorResponse): ApiError {
    val message = data.message
    return when (this) {
        HttpStatusCode.BadRequest -> BadRequestError(message)
        HttpStatusCode.NotFound -> NotFoundError(message)
        HttpStatusCode.InternalServerError -> InternalServerError(message)
        HttpStatusCode.Conflict -> ConflictError(message)
        HttpStatusCode.Unauthorized -> NotAuthenticatedError(message)
        else -> UnknownError(message)
    }
}