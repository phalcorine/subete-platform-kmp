package io.andromadus.subete.identity.plugins

import io.andromadus.subete.identity.data.dao.UserAccessTokenDao
import io.andromadus.subete.identity.data.dao.UserAccessTokenDaoImpl
import io.andromadus.subete.identity.data.dao.UserDao
import io.andromadus.subete.identity.data.dao.UserDaoImpl
import io.andromadus.subete.identity.rabbitmq.RabbitMQConnectionService
import io.andromadus.subete.identity.routes.AuthRouteApi
import io.andromadus.subete.identity.routes.AuthRouteApiContract
import io.andromadus.subete.identity.routes.admin.AdminModuleUserRouteApi
import io.andromadus.subete.identity.routes.admin.AdminModuleUserRouteApiContract
import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI(appConfig: AppConfig) {
    install(Koin) {
        slf4jLogger()

        val appModule = module {
            single { appConfig }

            singleOf(::RabbitMQConnectionService) {
                createdAtStart()
            }

            single<UserDao> { UserDaoImpl }
            single<UserAccessTokenDao> { UserAccessTokenDaoImpl }

            singleOf(::AuthRouteApi) { bind<AuthRouteApiContract>() }
            singleOf(::AdminModuleUserRouteApi) { bind<AdminModuleUserRouteApiContract>() }
        }

        modules(appModule)
    }
}