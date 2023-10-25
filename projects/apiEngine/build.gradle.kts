plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.9.20-RC"
    application
    id("io.ktor.plugin") version "2.3.5"
}

group = "engine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":shared"))
}

application {
    mainClass.set("io.andromadus.subete.engine.ServerKt")
}