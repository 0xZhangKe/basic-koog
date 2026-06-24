package com.zhangke.koog.basic.framework.http

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClientEngine {
    return OkHttp.create()
}
