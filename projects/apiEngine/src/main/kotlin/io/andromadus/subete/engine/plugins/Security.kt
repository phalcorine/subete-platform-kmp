package io.andromadus.subete.engine.plugins

import arrow.core.getOrElse
import io.andromadus.subete.api.models.AuthenticatedUserDto
import io.andromadus.subete.engine.data.repositories.AuthRepository
import io.andromadus.subete.jvm.infra.ktor.KtorAuthorizationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import org.koin.ktor.ext.inject

const val SECURITY_AUTH_USER = "auth-user"

data class AuthUserPrincipal(
    val userUid: String,
) : Principal

fun Application.configureSecurity() {
    val authRepository by inject<AuthRepository>()

    install(Authentication) {
        bearer(SECURITY_AUTH_USER) {
            realm = "Access to authenticated routes"
            authenticate { bearerTokenCredential ->
                val authenticatedUser = authRepository.checkAuth(bearerTokenCredential.token)
                    .getOrElse {
                        println("[Application::configureSecurity]")
                        println(it)
                        throw KtorAuthorizationException(it.message)
                    }

                authenticatedUser.toAuthUserPrincipal()
            }
        }
    }
}

fun AuthenticatedUserDto.toAuthUserPrincipal() = AuthUserPrincipal(userUid = uid)