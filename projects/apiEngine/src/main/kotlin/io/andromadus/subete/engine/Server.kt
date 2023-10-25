package io.andromadus.subete.engine

import io.andromadus.subete.engine.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val appConfig = loadConfiguration()
    configureDatabases(appConfig.database)
    val apiClient = configureApiClient()
    configureDI(appConfig, apiClient)
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}