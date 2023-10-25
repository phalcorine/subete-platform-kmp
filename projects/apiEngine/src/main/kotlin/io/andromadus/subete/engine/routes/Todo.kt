package io.andromadus.subete.engine.routes

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import io.andromadus.subete.jvm.api.*
import io.andromadus.subete.api.ApiDataResponse
import io.andromadus.subete.api.ApiSuccessMessage
import io.andromadus.subete.engine.data.dao.TodoDao
import io.andromadus.subete.engine.plugins.AuthUserPrincipal
import io.andromadus.subete.enums.TodoStatus
import io.andromadus.subete.jvm.infra.ktor.handleError
import io.andromadus.subete.jvm.utils.TokenGenerator
import io.andromadus.subete.models.CreateTodoDto
import io.andromadus.subete.models.Todo
import io.andromadus.subete.models.UpdateTodoDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.todoRoutes() {
    val todoRouteApi by inject<TodoRouteApiContract>()

    route("/todos") {
        get("/list") {
            either {
                val principal = call.principal<AuthUserPrincipal>()!!

                val response = todoRouteApi
                    .list(userUid = principal.userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        get("/find/uid/{uid}") {
            either {
                val uid = call.parameters["uid"]
                    ?: return@either BadRequestError(Messages.INVALID_ROUTE_PARAMETERS)

                val principal = call.principal<AuthUserPrincipal>()!!

                val response = todoRouteApi
                    .findByUid(uid, principal.userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        post("/create") {
            either {
                val requestBody = call.receive<CreateTodoRequestDto>()

                val principal = call.principal<AuthUserPrincipal>()!!

                val response = todoRouteApi
                    .create(requestBody, principal.userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.Created,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        patch("/update/{uid}") {
            either {
                val uid = call.parameters["uid"]
                    ?: return@either BadRequestError(Messages.INVALID_ROUTE_PARAMETERS)

                val requestBody = call.receive<UpdateTodoRequestDto>()

                val principal = call.principal<AuthUserPrincipal>()!!

                val response = todoRouteApi
                    .update(uid, requestBody, principal.userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }

        delete("/delete/{uid}") {
            either {
                val uid = call.parameters["uid"]
                    ?: return@either BadRequestError(Messages.INVALID_ROUTE_PARAMETERS)

                val principal = call.principal<AuthUserPrincipal>()!!

                val response = todoRouteApi
                    .delete(uid, principal.userUid)
                    .bind()

                call.respond(
                    status = HttpStatusCode.OK,
                    ApiDataResponse(response)
                )
            }.mapLeft { handleError(it) }
        }
    }
}

interface TodoRouteApiContract {
    suspend fun list(userUid: String): Either<ApiError, List<Todo>>
    suspend fun findByUid(uid: String, userUid: String): Either<ApiError, Todo>
    suspend fun create(data: CreateTodoRequestDto, userUid: String): Either<ApiError, Todo>
    suspend fun update(uid: String, data: UpdateTodoRequestDto, userUid: String): Either<ApiError, ApiSuccessMessage>
    suspend fun delete(uid: String, userUid: String): Either<ApiError, ApiSuccessMessage>
}

class TodoRouteApi(
    private val todoDao: TodoDao,
) : TodoRouteApiContract {
    override suspend fun list(userUid: String): Either<ApiError, List<Todo>> {
        val todos = todoDao.listByUserUid(userUid)

        return todos.right()
    }

    override suspend fun findByUid(uid: String, userUid: String): Either<ApiError, Todo> {
        val todo = validateTodoForUser(uid, userUid)
            .getOrElse { return it.left() }

        return todo.right()
    }

    override suspend fun create(data: CreateTodoRequestDto, userUid: String): Either<ApiError, Todo> {
        val createTodo = CreateTodoDto(
            uid = TokenGenerator.generateNewUid(),
            label = data.label,
            content = data.content,
            status = data.status,
            userUid = userUid,
        )

        val createdTodo = todoDao.create(createTodo)
            ?: return InternalServerError("An error occurred while attempting to create Todo!").left()

        return createdTodo.right()
    }

    override suspend fun update(uid: String, data: UpdateTodoRequestDto, userUid: String): Either<ApiError, ApiSuccessMessage> {
        val todo = validateTodoForUser(uid, userUid)
            .getOrElse { return it.left() }

        val updateTodo = UpdateTodoDto(
            label = data.label ?: todo.label,
            content = data.content ?: todo.content,
            status = data.status ?: todo.status,
        )

        todoDao.update(uid, updateTodo)

        return ApiSuccessMessage("Todo updated successfully!").right()
    }

    override suspend fun delete(uid: String, userUid: String): Either<ApiError, ApiSuccessMessage> {
        validateTodoForUser(uid, userUid)
            .getOrElse { return it.left() }

        todoDao.delete(uid)

        return ApiSuccessMessage("Todo deleted successfully!").right()
    }

    private suspend fun validateTodoForUser(uid: String, userUid: String): Either<ApiError, Todo> {
        val todo = todoDao.findByUid(uid)
            ?: return NotFoundError("Todo not found!").left()

        if (todo.userUid != userUid) {
            return NotFoundError("Todo not found!").left()
        }

        return todo.right()
    }
}

@Serializable
data class CreateTodoRequestDto(
    val label: String,
    val content: String,
    val status: TodoStatus
)

@Serializable
data class UpdateTodoRequestDto(
    val label: String? = null,
    val content: String? = null,
    val status: TodoStatus? = null,
)