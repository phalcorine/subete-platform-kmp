package io.andromadus.subete.jvm.utils

import java.util.UUID

object TokenGenerator {
    fun generateNewUid(): String = UUID.randomUUID().toString()
    fun generateAccessToken(): String = UUID.randomUUID().toString()
}