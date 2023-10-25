package io.andromadus.subete.models

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String,
    @SerialName("full_name")
    val fullName: String,
    val username: String,
    val password: String,
    val suspended: Boolean,
    @SerialName("created_at")
    val createdAt: LocalDateTime,
    @SerialName("last_modified_at")
    val lastModifiedAt: LocalDateTime,
)

data class CreateUserDto(
    val uid: String,
    val fullName: String,
    val username: String,
    val password: String,
    val suspended: Boolean,
)

data class UpdateUserDto(
    val fullName: String,
    val username: String,
    val password: String,
    val suspended: Boolean,
)

// Mappers

fun User.toUpdateUserDto() = UpdateUserDto(
    fullName, username, password, suspended
)