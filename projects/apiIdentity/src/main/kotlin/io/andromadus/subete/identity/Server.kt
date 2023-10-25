package io.andromadus.subete.identity

import io.andromadus.subete.identity.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val appConfig = loadConfiguration()
    configureDatabases(appConfig.database)
    configureDI(appConfig)
    configureMonitoring()
    configureHTTP()
    configureSerialization()
    configureSecurity()
    configureRouting()
}