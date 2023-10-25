package io.andromadus.subete.identity.plugins

import io.ktor.server.application.*

data class DatabaseConfig(
    val driverClassName: String,
    val url: String,
    val user: String,
    val password: String
)

data class RabbitMQConfig(
    val host: String,
    val username: String,
    val password: String,
    val queues: QueueConfig,
) {
    data class QueueConfig(
        val general: String,
    )
}

data class AppConfig(
    val database: DatabaseConfig,
    val rabbitmq: RabbitMQConfig,
)

fun Application.loadConfiguration(): AppConfig {
    val driverClassName = environment.config.property("database.driverClassName").getString()
    val jdbcUrl = environment.config.property("database.url").getString()
    val jdbcUser = environment.config.property("database.user").getString()
    val jdbcPassword = environment.config.property("database.password").getString()
    val databaseConfig = DatabaseConfig(
        driverClassName = driverClassName,
        url = jdbcUrl,
        user = jdbcUser,
        password = jdbcPassword,
    )

    val rabbitMQHost = environment.config.property("rabbitmq.host").getString()
    val rabbitMQUsername = environment.config.property("rabbitmq.username").getString()
    val rabbitMQPassword = environment.config.property("rabbitmq.password").getString()
    val rabbitMQueueConfigGeneral = environment.config.property("rabbitmq.queues.general").getString()
    val rabbitMQConfig = RabbitMQConfig(
        host = rabbitMQHost,
        username = rabbitMQUsername,
        password = rabbitMQPassword,
        queues = RabbitMQConfig.QueueConfig(
            general = rabbitMQueueConfigGeneral,
        ),
    )

    return AppConfig(
        database = databaseConfig,
        rabbitmq = rabbitMQConfig,
    )
}