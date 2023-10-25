package io.andromadus.subete.engine.plugins

import io.andromadus.subete.jvm.infra.ktor.jsonSerializationConfig
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*

fun Application.configureApiClient(): HttpClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(jsonSerializationConfig)
        }

        engine {
            config {
                followRedirects(true)
            }
        }
    }

    return client
}