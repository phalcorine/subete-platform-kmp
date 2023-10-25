package io.andromadus.subete.models

import io.andromadus.subete.enums.TodoStatus
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val uid: String,
    val label: String,
    val content: String,
    val status: TodoStatus,
    @SerialName("created_at")
    val createdAt: LocalDateTime,
    @SerialName("last_modified_at")
    val lastModifiedAt: LocalDateTime,
    // relations
    @SerialName("user_uid")
    val userUid: String,
)

data class CreateTodoDto(
    val uid: String,
    val label: String,
    val content: String,
    val status: TodoStatus,
    val userUid: String,
)

data class UpdateTodoDto(
    val label: String,
    val content: String,
    val status: TodoStatus,
)