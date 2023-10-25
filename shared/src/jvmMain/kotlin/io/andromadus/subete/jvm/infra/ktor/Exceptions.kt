package io.andromadus.subete.jvm.infra.ktor

data class KtorAuthorizationException(override val message: String?) : RuntimeException()