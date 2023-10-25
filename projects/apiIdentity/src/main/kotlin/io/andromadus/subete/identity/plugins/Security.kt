package io.andromadus.subete.identity.plugins

import io.andromadus.subete.identity.data.dao.UserAccessTokenDao
import io.andromadus.subete.identity.data.dao.UserDao
import io.andromadus.subete.jvm.api.Messages
import io.andromadus.subete.jvm.infra.ktor.KtorAuthorizationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

const val SECURITY_AUTH_USER = "auth-user"

@Serializable
data class AuthUserPrincipal(
    val uid: String,
) : Principal

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()
    val userAccessTokenDao by inject<UserAccessTokenDao>()

    install(Authentication) {
        bearer(SECURITY_AUTH_USER) {
            realm = "Access to authenticated routes"
            authenticate { bearerTokenCredential ->
                val userAccessToken = userAccessTokenDao
                    .findByAccessToken(bearerTokenCredential.token)
                    ?: throw KtorAuthorizationException(Messages.USER_NOT_AUTHENTICATED)

                val user = userDao
                    .findByUid(userAccessToken.userUid)
                    ?: throw KtorAuthorizationException(Messages.USER_NOT_AUTHENTICATED)

                if (user.suspended) {
                    throw KtorAuthorizationException(Messages.USER_ACCOUNT_SUSPENDED)
                }

                AuthUserPrincipal(uid = user.uid)
            }
        }
    }
}