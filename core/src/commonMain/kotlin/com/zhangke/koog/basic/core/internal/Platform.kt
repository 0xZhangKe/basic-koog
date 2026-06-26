package com.zhangke.koog.basic.core.internal

import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    val name: String,
)

expect fun currentPlatform(): Platform
