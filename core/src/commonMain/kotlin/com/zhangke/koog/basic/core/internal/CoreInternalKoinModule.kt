package com.zhangke.koog.basic.core.internal

import com.zhangke.koog.basic.core.internal.download.FileDownloader
import com.zhangke.koog.basic.core.internal.http.sharedHttpClient
import com.zhangke.koog.basic.core.internal.storage.LocalStorage
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreInternalKoinModule = module {
    createPlatformModule()
    single<HttpClient> { sharedHttpClient }
    singleOf(::FileDownloader)
    singleOf(::LocalStorage)
}

expect fun Module.createPlatformModule()
