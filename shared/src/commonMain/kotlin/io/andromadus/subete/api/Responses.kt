package io.andromadus.subete.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val message: String,
)

@Serializable
data class ApiSuccessMessage(
    val message: String,
)

@Serializable
data class ApiDataResponse<T>(
    val data: T,
    val message: String = "Successful",
)