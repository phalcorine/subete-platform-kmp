package io.andromadus.subete.identity.data.dao

import io.andromadus.subete.identity.data.entities.UserAccessTokenTable
import io.andromadus.subete.identity.data.entities.toUserAccessToken
import io.andromadus.subete.identity.domain.models.CreateUserAccessToken
import io.andromadus.subete.identity.domain.models.UserAccessToken
import io.andromadus.subete.jvm.persistence.dbQuery
import io.andromadus.subete.jvm.utils.getLocalDateTimeNow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface UserAccessTokenDao {
    suspend fun findByAccessToken(accessToken: String): UserAccessToken?
    suspend fun create(data: CreateUserAccessToken): UserAccessToken?
    suspend fun deleteByAccessToken(accessToken: String)
    suspend fun deleteByUserUid(userUid: String)
}

object UserAccessTokenDaoImpl : UserAccessTokenDao {
    override suspend fun findByAccessToken(accessToken: String): UserAccessToken? {
        return dbQuery {
            UserAccessTokenTable
                .select { UserAccessTokenTable.access_token eq accessToken }
                .firstOrNull()
                ?.toUserAccessToken()
        }
    }

    override suspend fun create(data: CreateUserAccessToken): UserAccessToken? {
        return dbQuery {
            UserAccessTokenTable
                .insert {
                    val now = getLocalDateTimeNow()

                    it[access_token] = data.accessToken
                    it[expires_in_interval] = data.expiresInInterval
                    it[user_uid] = data.userUid
                    it[created_at] = now
                }.resultedValues
                ?.firstOrNull()
                ?.toUserAccessToken()
        }
    }

    override suspend fun deleteByAccessToken(accessToken: String) {
        return dbQuery {
            UserAccessTokenTable
                .deleteWhere { access_token eq accessToken }
        }
    }

    override suspend fun deleteByUserUid(userUid: String) {
        return dbQuery {
            UserAccessTokenTable
                .deleteWhere { user_uid eq userUid }
        }
    }
}