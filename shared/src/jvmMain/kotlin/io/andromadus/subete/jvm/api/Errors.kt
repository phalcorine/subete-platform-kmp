package io.andromadus.subete.jvm.api

sealed interface ApiError {
    val message: String
}

data class NotAuthenticatedError(override val message: String) : ApiError
data class BadRequestError(override val message: String) : ApiError
data class ConflictError(override val message: String) : ApiError
data class NotFoundError(override val message: String) : ApiError
data class InternalServerError(override val message: String) : ApiError
data class UnknownError(override val message: String) : ApiError