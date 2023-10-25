package io.andromadus.subete.engine.plugins

import io.andromadus.subete.jvm.infra.ktor.jsonSerializationConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(jsonSerializationConfig)
    }
}