package io.andromadus.subete.identity.routes.admin

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import io.andromadus.subete.api.ApiDataResponse
import io.andromadus.subete.api.ApiSuccessMessage
import io.andromadus.subete.identity.data.dao.UserDao
import io.andromadus.subete.jvm.api.ApiError
import io.andromadus.subete.jvm.api.BadRequestError
import io.andromadus.subete.jvm.api.Messages
import io.andromadus.subete.jvm.api.NotFoundError
import io.andromadus.subete.jvm.infra.ktor.handleError
import io.andromadus.subete.models.toUpdateUserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.adminModuleUserRoutes() {
    val adminModuleUserRouteApi by inject<AdminModuleUserRouteApiContract>()

    route("/users") {
        get("/toggle-suspension/{userUid}") {
            either {
                val userUid = call.parameters["userUid"]
                    ?: return@either BadRequestError(Messages.INVALID_ROUTE_PARAMETERS)

                val response = adminModuleUserRouteApi
                    .toggleUserSuspension(userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }
    }
}

interface AdminModuleUserRouteApiContract {
    suspend fun toggleUserSuspension(subjectUserUid: String): Either<ApiError, ApiSuccessMessage>
}

class AdminModuleUserRouteApi(
    private val userDao: UserDao
) : AdminModuleUserRouteApiContract {
    override suspend fun toggleUserSuspension(subjectUserUid: String): Either<ApiError, ApiSuccessMessage> {
        val user = userDao
            .findByUid(subjectUserUid)
            ?: return NotFoundError("User not found!").left()

        val updateUser = user
            .toUpdateUserDto()
            .copy(
                suspended = user.suspended.not()
            )

        userDao
            .update(subjectUserUid, updateUser)

        val responseMessage = "User suspension ${if (updateUser.suspended) "activated" else "deactivated"}!"
        return ApiSuccessMessage(responseMessage).right()
    }
}