package io.andromadus.subete.engine.plugins

import io.andromadus.subete.engine.data.entities.TodoTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases(databaseConfig: DatabaseConfig) {

    Database.connect(
        driver = databaseConfig.driverClassName,
        url = databaseConfig.url,
        user = databaseConfig.user,
        password = databaseConfig.password
    )

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.createMissingTablesAndColumns(
            TodoTable
        )
    }
}