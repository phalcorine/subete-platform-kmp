package io.andromadus.subete.jvm.infra.ktor

import kotlinx.serialization.json.Json

val jsonSerializationConfig = Json {
    prettyPrint = true
    isLenient = true
}