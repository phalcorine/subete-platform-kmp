package io.andromadus.subete.jvm.utils

import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

fun getLocalDateTimeNow() = LocalDateTime.now().toKotlinLocalDateTime()