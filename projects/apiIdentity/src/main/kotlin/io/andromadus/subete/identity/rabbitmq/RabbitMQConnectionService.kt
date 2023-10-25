package io.andromadus.subete.identity.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import io.andromadus.subete.identity.plugins.AppConfig
import io.andromadus.subete.jvm.infra.rabbitmq.QueueEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RabbitMQConnectionService(
    private val appConfig: AppConfig
) {
    private val channel: Channel
    private val queueGeneral: String

    init {
        val rabbitMQConfig = appConfig.rabbitmq
        val connectionFactory = ConnectionFactory().apply {
            username = rabbitMQConfig.username
            password = rabbitMQConfig.password
            host = rabbitMQConfig.host
            virtualHost = ConnectionFactory.DEFAULT_VHOST
            port = ConnectionFactory.DEFAULT_AMQP_PORT
        }

        val channel = connectionFactory
            .newConnection()
            .createChannel()

        channel.queueDeclare(
            rabbitMQConfig.queues.general,
            true,
            false,
            false,
            null
        )

        this.channel = channel
        this.queueGeneral = rabbitMQConfig.queues.general
    }

    fun sendMessageToGeneralQueue(event: QueueEvent) {
        channel.basicPublish(
            "",
            queueGeneral,
            null,
            Json.encodeToString(event).toByteArray()
        )
    }
}