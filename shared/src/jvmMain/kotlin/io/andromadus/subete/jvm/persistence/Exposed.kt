package io.andromadus.subete.jvm.persistence

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(block: () -> T): T = newSuspendedTransaction { block() }