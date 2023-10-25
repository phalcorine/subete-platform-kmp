package io.andromadus.subete.identity.domain.models

import kotlinx.datetime.LocalDateTime

data class UserAccessToken(
    val accessToken: String,
    val expiresInInterval: Long,
    val createdAt: LocalDateTime,
    // relations
    val userUid: String,
)

data class CreateUserAccessToken(
    val accessToken: String,
    val expiresInInterval: Long,
    // relations
    val userUid: String,
)