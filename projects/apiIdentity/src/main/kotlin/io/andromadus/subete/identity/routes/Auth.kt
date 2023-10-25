package io.andromadus.subete.identity.routes

import arrow.core.Either
import arrow.core.bisequenceNullable
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import io.andromadus.subete.jvm.api.*
import io.andromadus.subete.jvm.infra.ktor.handleError
import io.andromadus.subete.api.ApiDataResponse
import io.andromadus.subete.api.ApiErrorResponse
import io.andromadus.subete.api.models.AuthenticatedUserDto
import io.andromadus.subete.identity.data.dao.UserAccessTokenDao
import io.andromadus.subete.identity.data.dao.UserDao
import io.andromadus.subete.identity.domain.models.CreateUserAccessToken
import io.andromadus.subete.identity.plugins.AuthUserPrincipal
import io.andromadus.subete.identity.plugins.SECURITY_AUTH_USER
import io.andromadus.subete.identity.rabbitmq.RabbitMQConnectionService
import io.andromadus.subete.jvm.infra.rabbitmq.DefaultQueueEvent
import io.andromadus.subete.jvm.utils.TokenGenerator
import io.andromadus.subete.models.CreateUserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

fun Route.authRoutes() {
    val authRouteApi by inject<AuthRouteApiContract>()

    route("/auth") {
        post("/login") {
            either {
                val requestDto = call.receive<AuthLoginRequestDto>()

                val response = authRouteApi
                    .login(requestDto)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        post("/signup") {
            either {
                val requestDto = call.receive<AuthSignupRequestDto>()

                val response = authRouteApi
                    .signup(requestDto)
                    .bind()

                call.respond(
                    status = HttpStatusCode.Created,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        authenticate(SECURITY_AUTH_USER) {
            get("/check-auth") {
                either {
                    val principal = call.principal<AuthUserPrincipal>()
                        ?: return@get call.respond(
                            status = HttpStatusCode.OK,
                            ApiErrorResponse(Messages.AUTHENTICATION_NOT_SET)
                        )

                    val response = authRouteApi
                        .checkAuth(userUid = principal.uid)
                        .bind()

                    call.respond(
                        status = HttpStatusCode.OK,
                        ApiDataResponse(response)
                    )
                }.mapLeft { handleError(it) }
            }
        }
    }
}

interface AuthRouteApiContract {
    suspend fun login(request: AuthLoginRequestDto): Either<ApiError, AuthLoginResponseDto>
    suspend fun signup(request: AuthSignupRequestDto): Either<ApiError, AuthSignupResponseDto>
    suspend fun checkAuth(userUid: String): Either<ApiError, AuthenticatedUserDto>
}

class AuthRouteApi(
    private val userDao: UserDao,
    private val userAccessTokenDao: UserAccessTokenDao,
    private val rabbitMQConnectionService: RabbitMQConnectionService
) : AuthRouteApiContract {
    override suspend fun login(request: AuthLoginRequestDto): Either<ApiError, AuthLoginResponseDto> {
        val user = userDao.findByUsername(request.username)
            ?: return NotAuthenticatedError(Messages.INVALID_USERNAME_OR_PASSWORD).left()
        if (user.password != request.password) {
            return NotAuthenticatedError(Messages.INVALID_USERNAME_OR_PASSWORD).left()
        }

        // Generate some token
        val createUserAccessToken = CreateUserAccessToken(
            accessToken = TokenGenerator.generateAccessToken(),
            expiresInInterval = 1.days.inWholeMinutes,
            userUid = user.uid,
        )

        // Delete previous token
        userAccessTokenDao.deleteByUserUid(user.uid)

        val accessToken = userAccessTokenDao
            .create(createUserAccessToken)
            ?: return InternalServerError("An error occurred while attempting to persist access token!").left()

        val response = AuthLoginResponseDto(
            accessToken = accessToken.accessToken,
            user = AuthLoggedInUserDto(
                uid = user.uid,
                fullName = user.fullName,
                username = user.username
            )
        )

        return response.right()
    }

    override suspend fun signup(request: AuthSignupRequestDto): Either<ApiError, AuthSignupResponseDto> {
        val existingUser = userDao.findByUsername(request.username)
        if (existingUser != null) {
            return ConflictError("A user with the supplied username already exists!").left()
        }

        val createUserDto = CreateUserDto(
            uid = TokenGenerator.generateNewUid(),
            username = request.username,
            password = request.password,
            fullName = request.fullName,
            suspended = true,
        )

        val user = userDao.create(createUserDto)
            ?: return InternalServerError("An error occurred while attempting to create a user!").left()

        val response = AuthSignupResponseDto(
            userUid = user.uid,
            suspended = user.suspended,
        )

        return response.right()
    }

    override suspend fun checkAuth(userUid: String): Either<ApiError, AuthenticatedUserDto> {
        val response = AuthenticatedUserDto(uid = userUid)

        rabbitMQConnectionService.sendMessageToGeneralQueue(DefaultQueueEvent("A user with UID: $userUid was just authenticated!"))

        return response.right()
    }
}

@Serializable
data class AuthLoginRequestDto(
    val username: String,
    val password: String,
)

@Serializable
data class AuthLoginResponseDto(
    @SerialName("access_token")
    val accessToken: String,
    val user: AuthLoggedInUserDto,
)

@Serializable
data class AuthLoggedInUserDto(
    val uid: String,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
)

@Serializable
data class AuthSignupRequestDto(
    @SerialName("full_name")
    val fullName: String,
    val username: String,
    val password: String,
)

@Serializable
data class AuthSignupResponseDto(
    @SerialName("user_uid")
    val userUid: String,
    val suspended: Boolean,
)