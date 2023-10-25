package io.andromadus.subete.engine.data.entities

import io.andromadus.subete.enums.TodoStatus
import io.andromadus.subete.models.Todo
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object TodoTable : IntIdTable("todos") {
    val uid = varchar("uid", 50).uniqueIndex("idx_todos_uid")
    val label = varchar("label", 100)
    val content = varchar("content", 1000)
    val status = varchar("status", 30)
    val created_at = datetime("created_at")
    val last_modified_at = datetime("last_modified_at")
    // relations
    val user_uid = varchar("user_uid", 50)
}

fun ResultRow.toTodo() = Todo(
    uid = this[TodoTable.uid],
    label = this[TodoTable.label],
    content = this[TodoTable.content],
    status = TodoStatus.strictValueOf(this[TodoTable.status]),
    createdAt = this[TodoTable.created_at],
    lastModifiedAt = this[TodoTable.last_modified_at],
    userUid = this[TodoTable.user_uid]
)