package io.andromadus.subete.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TodoStatus(val status: String) {
    @SerialName("draft")
    DRAFT("draft"),
    @SerialName("pending")
    PENDING("pending"),
    @SerialName("completed")
    COMPLETED("completed"),
    @SerialName("archived")
    ARCHIVED("archived"),
    @SerialName("unknown")
    UNKNOWN("unknown");

    companion object {
        fun strictValueOf(status: String): TodoStatus = entries.firstOrNull { it.status == status } ?: UNKNOWN
    }
}