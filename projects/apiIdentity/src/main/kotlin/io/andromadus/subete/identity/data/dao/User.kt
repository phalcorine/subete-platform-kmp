package io.andromadus.subete.identity.data.dao

import io.andromadus.subete.identity.data.entities.UserTable
import io.andromadus.subete.identity.data.entities.toUser
import io.andromadus.subete.jvm.persistence.dbQuery
import io.andromadus.subete.jvm.utils.getLocalDateTimeNow
import io.andromadus.subete.models.CreateUserDto
import io.andromadus.subete.models.UpdateUserDto
import io.andromadus.subete.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface UserDao {
    suspend fun list(): List<User>
    suspend fun findByUid(uid: String): User?
    suspend fun findByUsername(username: String): User?
    suspend fun create(data: CreateUserDto): User?
    suspend fun update(uid: String, data: UpdateUserDto)
    suspend fun delete(uid: String)
}

object UserDaoImpl : UserDao {
    override suspend fun list(): List<User> {
        return dbQuery {
            UserTable
                .selectAll()
                .map { it.toUser() }
        }
    }

    override suspend fun findByUid(uid: String): User? {
        return dbQuery {
            UserTable
                .select { UserTable.uid eq uid }
                .firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun findByUsername(username: String): User? {
        return dbQuery {
            UserTable
                .select { UserTable.username eq username }
                .firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun create(data: CreateUserDto): User? {
        return dbQuery {
            UserTable
                .insert {
                    val now = getLocalDateTimeNow()

                    it[uid] = data.uid
                    it[username] = data.username
                    it[password] = data.password
                    it[full_name] = data.fullName
                    it[suspended] = data.suspended
                    it[created_at] = now
                    it[last_modified_at] = now
                }.resultedValues
                ?.firstOrNull()
                ?.toUser()
        }
    }

    override suspend fun update(uid: String, data: UpdateUserDto) {
        return dbQuery {
            UserTable
                .update({ UserTable.uid eq uid }) {
                    val now = getLocalDateTimeNow()

                    it[username] = data.username
                    it[password] = data.password
                    it[full_name] = data.fullName
                    it[suspended] = data.suspended
                    it[last_modified_at] = now
                }
        }
    }

    override suspend fun delete(uid: String) {
        return dbQuery {
            UserTable
                .deleteWhere { UserTable.uid eq uid }
        }
    }
}