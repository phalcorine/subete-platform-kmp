package io.andromadus.subete.api.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticatedUserDto(
    val uid: String,
)