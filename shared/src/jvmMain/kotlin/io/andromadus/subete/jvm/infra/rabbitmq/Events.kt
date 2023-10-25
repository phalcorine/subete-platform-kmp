package io.andromadus.subete.jvm.infra.rabbitmq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class QueueEvent {
    abstract val message: String
}

@Serializable
@SerialName("default")
data class DefaultQueueEvent(override val message: String) : QueueEvent()