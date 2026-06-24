package com.zhangke.koog.basic.framework.utils

import java.nio.file.Paths

actual fun getProjectFolderPath(): String {
    val userHome = System.getProperty("user.home")
    val osName = System.getProperty("os.name")
    val basePath = if (osName.contains("Mac", ignoreCase = true)) {
        Paths.get(userHome, "Library", "Application Support")
    } else {
        Paths.get(userHome)
    }

    return basePath
        .resolve("BasicAgent")
        .toString()
}
