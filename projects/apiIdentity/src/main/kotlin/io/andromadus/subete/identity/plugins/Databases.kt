package io.andromadus.subete.identity.plugins

import io.andromadus.subete.identity.data.entities.UserAccessTokenTable
import io.andromadus.subete.identity.data.entities.UserTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases(databaseConfig: DatabaseConfig) {
    Database.connect(
        url = databaseConfig.url,
        driver = databaseConfig.driverClassName,
        user = databaseConfig.user,
        password = databaseConfig.password
    )

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.createMissingTablesAndColumns(
            UserTable,
            UserAccessTokenTable,
        )
    }
}