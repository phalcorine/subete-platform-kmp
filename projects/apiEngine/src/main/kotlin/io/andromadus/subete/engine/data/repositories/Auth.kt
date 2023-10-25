package io.andromadus.subete.engine.data.repositories

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.andromadus.subete.api.ApiDataResponse
import io.andromadus.subete.api.ApiErrorResponse
import io.andromadus.subete.api.models.AuthenticatedUserDto
import io.andromadus.subete.engine.plugins.AppConfig
import io.andromadus.subete.jvm.api.ApiError
import io.andromadus.subete.jvm.infra.ktor.toApiError
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthRepository {
    suspend fun checkAuth(bearerToken: String): Either<ApiError, AuthenticatedUserDto>
}

class AuthRepositoryImpl(
    private val appConfig: AppConfig,
    private val httpClient: HttpClient
) : AuthRepository {
    override suspend fun checkAuth(bearerToken: String): Either<ApiError, AuthenticatedUserDto> {
        val authBaseUrl = appConfig.subete.authBaseUrl
        val rawResponse = httpClient.get("$authBaseUrl/auth/check-auth") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            headers {
                set(HttpHeaders.Authorization, "Bearer $bearerToken")
            }
        }
        return if (rawResponse.status.isSuccess()) {
            val responseBody = rawResponse.body<ApiDataResponse<AuthenticatedUserDto>>()
            responseBody.data.right()
        } else {
            val responseBody = rawResponse.body<ApiErrorResponse>()
            rawResponse.status.toApiError(responseBody).left()
        }
    }
}