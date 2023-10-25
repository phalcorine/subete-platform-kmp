plugins {
    kotlin("multiplatform") version "1.9.20-RC"
    kotlin("plugin.serialization") version "1.9.20-RC"
}

// Properties
val arrow_version: String by project
val exposed_version: String by project
val koin_ktor_version: String by project
val kotlinx_datetime_version: String by project
val kotlinx_serialization_version: String by project
val ktor_version: String by project
val logback_version: String by project
val mysql_version: String by project
val postgresql_version: String by project
val rabbitmq_version: String by project

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(17)
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.arrow-kt:arrow-core:$arrow_version")
                api("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinx_datetime_version")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

                // Ktor
                api("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                api("io.ktor:ktor-client-core:$ktor_version")
                api("io.ktor:ktor-client-content-negotiation:$ktor_version")
                api("io.ktor:ktor-client-content-negotiation:$ktor_version")
            }
        }

        val jvmMain by getting {
            dependencies {
                // Ktor
                api("io.ktor:ktor-server-core:$ktor_version")
                api("io.ktor:ktor-server-netty:$ktor_version")
                api("io.ktor:ktor-server-config-yaml:$ktor_version")
                api("io.ktor:ktor-server-content-negotiation:$ktor_version")
                api("io.ktor:ktor-server-auth:$ktor_version")
                api("io.ktor:ktor-server-swagger:$ktor_version")
                api("io.ktor:ktor-server-cors:$ktor_version")
                api("io.ktor:ktor-server-status-pages:$ktor_version")
                api("io.ktor:ktor-server-call-logging:$ktor_version")
                // Ktor-Client
                api("io.ktor:ktor-client-okhttp:$ktor_version")
                // Logback
                api("ch.qos.logback:logback-classic:$logback_version")
                // Exposed
                api("org.jetbrains.exposed:exposed-core:$exposed_version")
                api("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
                api("org.jetbrains.exposed:exposed-dao:$exposed_version")
                api("org.jetbrains.exposed:exposed-json:$exposed_version")
                api("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
                // MySQL
                api("com.mysql:mysql-connector-j:$mysql_version")
                // PostgreSQL
                api("org.postgresql:postgresql:$postgresql_version")
                // Koin-Ktor
                api("io.insert-koin:koin-ktor:$koin_ktor_version")
                api("io.insert-koin:koin-logger-slf4j:$koin_ktor_version")
                // RabbitMQ
                api("com.rabbitmq:amqp-client:$rabbitmq_version")
            }
        }
    }
}