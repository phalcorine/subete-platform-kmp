package io.andromadus.subete.engine.plugins

import io.andromadus.subete.engine.data.dao.TodoDao
import io.andromadus.subete.engine.data.dao.TodoDaoImpl
import io.andromadus.subete.engine.data.repositories.AuthRepository
import io.andromadus.subete.engine.data.repositories.AuthRepositoryImpl
import io.andromadus.subete.engine.rabbitmq.RabbitMQConnectionService
import io.andromadus.subete.engine.routes.TodoRouteApi
import io.andromadus.subete.engine.routes.TodoRouteApiContract
import io.ktor.client.*
import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI(appConfig: AppConfig, httpClient: HttpClient) {
    install(Koin) {
        slf4jLogger()

        val appModule = module {
            single { appConfig }
            single { httpClient }

            singleOf(::RabbitMQConnectionService) {
                createdAtStart()
            }

            single<TodoDao> { TodoDaoImpl }
            singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }

            singleOf(::TodoRouteApi) { bind<TodoRouteApiContract>() }
        }

        modules(appModule)
    }
}