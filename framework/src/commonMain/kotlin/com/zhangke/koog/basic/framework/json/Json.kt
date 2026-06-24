package com.zhangke.koog.basic.framework.json

import kotlinx.serialization.json.Json

val globalJson: Json by lazy {
    Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
}
