plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.20-RC"
    application
    id("io.ktor.plugin") version "2.3.5"
}

group = "identity"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":shared"))
}

application {
    mainClass.set("io.andromadus.subete.auth.ServerKt")
}