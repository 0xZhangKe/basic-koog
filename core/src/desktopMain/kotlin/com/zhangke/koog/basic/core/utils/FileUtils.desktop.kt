package com.zhangke.koog.basic.core.utils

import com.zhangke.koog.basic.core.internal.utils.getProjectFolderPath
import java.nio.file.Path
import java.nio.file.Paths

actual fun createBuildFolderPath(subfolder: String?): Path {
    val path = Paths.get(getProjectFolderPath())
    return if (subfolder.isNullOrEmpty() || subfolder.isBlank()) {
        path
    } else {
        path.resolve(subfolder)
    }
}
