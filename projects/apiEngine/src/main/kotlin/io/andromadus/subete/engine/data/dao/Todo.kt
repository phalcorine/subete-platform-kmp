package io.andromadus.subete.engine.data.dao

import io.andromadus.subete.engine.data.entities.TodoTable
import io.andromadus.subete.engine.data.entities.toTodo
import io.andromadus.subete.jvm.persistence.dbQuery
import io.andromadus.subete.jvm.utils.getLocalDateTimeNow
import io.andromadus.subete.models.CreateTodoDto
import io.andromadus.subete.models.Todo
import io.andromadus.subete.models.UpdateTodoDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

interface TodoDao {
    suspend fun listByUserUid(userUid: String): List<Todo>
    suspend fun findByUid(uid: String): Todo?
    suspend fun create(data: CreateTodoDto): Todo?
    suspend fun update(uid: String, data: UpdateTodoDto)
    suspend fun delete(uid: String)
}

object TodoDaoImpl : TodoDao {
    override suspend fun listByUserUid(userUid: String): List<Todo> {
        return dbQuery {
            TodoTable
                .select { TodoTable.user_uid eq userUid }
                .map { it.toTodo() }
        }
    }

    override suspend fun findByUid(uid: String): Todo? {
        return dbQuery {
            TodoTable
                .select { TodoTable.uid eq uid }
                .firstOrNull()
                ?.toTodo()
        }
    }

    override suspend fun create(data: CreateTodoDto): Todo? {
        return dbQuery {
            TodoTable
                .insert {
                    val now = getLocalDateTimeNow()

                    it[uid] = data.uid
                    it[label] = data.label
                    it[content] = data.content
                    it[status] = data.status.status
                    it[user_uid] = data.userUid
                    it[created_at] = now
                    it[last_modified_at] = now
                }.resultedValues
                ?.firstOrNull()
                ?.toTodo()
        }
    }

    override suspend fun update(uid: String, data: UpdateTodoDto) {
        return dbQuery {
            TodoTable
                .update({ TodoTable.uid eq uid }) {
                    val now = getLocalDateTimeNow()

                    it[label] = data.label
                    it[content] = data.content
                    it[status] = data.status.status
                    it[last_modified_at] = now
                }
        }
    }

    override suspend fun delete(uid: String) {
        return dbQuery {
            TodoTable
                .deleteWhere { TodoTable.uid eq uid }
        }
    }
}