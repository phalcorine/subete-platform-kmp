package io.andromadus.subete.identity.data.entities

import io.andromadus.subete.models.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : IntIdTable("users") {
    val uid = varchar("uid", 50).uniqueIndex("idx_users_uid")
    val username = varchar("username", 200).uniqueIndex("idx_users_username")
    val password = varchar("password", 4096)
    val full_name = varchar("full_name", 100)
    val suspended = bool("suspended")
    val created_at = datetime("created_at")
    val last_modified_at = datetime("last_modified_at")
}

fun ResultRow.toUser() = User(
    uid = this[UserTable.uid],
    username = this[UserTable.username],
    password = this[UserTable.password],
    fullName = this[UserTable.full_name],
    suspended = this[UserTable.suspended],
    createdAt = this[UserTable.created_at],
    lastModifiedAt = this[UserTable.last_modified_at]
)