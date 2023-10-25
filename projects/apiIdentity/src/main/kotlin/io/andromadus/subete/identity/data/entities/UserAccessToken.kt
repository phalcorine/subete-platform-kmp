package io.andromadus.subete.identity.data.entities

import io.andromadus.subete.identity.domain.models.UserAccessToken
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserAccessTokenTable : IntIdTable("user_access_tokens") {
    val access_token = varchar("access_token", 50)
    val expires_in_interval = long("expires_in_interval")
    val created_at = datetime("created_at")
    // relations
    val user_uid = reference("user_uid", UserTable.uid)
}

fun ResultRow.toUserAccessToken() = UserAccessToken(
    accessToken = this[UserAccessTokenTable.access_token],
    expiresInInterval = this[UserAccessTokenTable.expires_in_interval],
    createdAt = this[UserAccessTokenTable.created_at],
    userUid = this[UserAccessTokenTable.user_uid]
)