@file:JvmName("FrameworkDesktopKoinModuleKt")

package com.zhangke.koog.basic.framework

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.bind
import java.util.prefs.Preferences

actual fun Module.createPlatformModule() {
    factory {
        val preferences = Preferences.userRoot().node("com/zhangke/koog/basic")
        PreferencesSettings(preferences)
    } bind Settings::class
}
