package com.zhangke.koog.basic.framework

import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    val name: String,
)

expect fun currentPlatform(): Platform
