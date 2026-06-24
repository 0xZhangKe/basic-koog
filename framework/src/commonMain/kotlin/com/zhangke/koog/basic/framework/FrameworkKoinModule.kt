package com.zhangke.koog.basic.framework

import com.zhangke.koog.basic.framework.download.FileDownloader
import com.zhangke.koog.basic.framework.http.sharedHttpClient
import com.zhangke.koog.basic.framework.storage.LocalStorage
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val frameworkKoinModule = module {
    createPlatformModule()
    single<HttpClient> { sharedHttpClient }
    singleOf(::FileDownloader)
    singleOf(::LocalStorage)
}

expect fun Module.createPlatformModule()
