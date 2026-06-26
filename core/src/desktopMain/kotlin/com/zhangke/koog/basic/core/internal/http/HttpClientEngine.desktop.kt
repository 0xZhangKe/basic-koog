package com.zhangke.koog.basic.core.internal.http

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClientEngine {
    return OkHttp.create()
}
