package io.andromadus.subete.engine.rabbitmq

import com.rabbitmq.client.*
import io.andromadus.subete.engine.plugins.AppConfig
import io.andromadus.subete.jvm.infra.rabbitmq.QueueEvent
import kotlinx.serialization.json.Json

class RabbitMQConnectionService(
    private val appConfig: AppConfig
) {
    private val channel: Channel

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
        val queueGeneral = rabbitMQConfig.queues.general

        val queueConsumerGeneral = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?
            ) {
                body?.let {
                    val payload = String(it)
                    val queueEvent = Json.decodeFromString<QueueEvent>(payload)
                    println("Received Message from Queue [${queueGeneral}]")
                    println(queueEvent)
                }
            }
        }

        channel.basicConsume(queueGeneral, true, queueConsumerGeneral)
    }
}