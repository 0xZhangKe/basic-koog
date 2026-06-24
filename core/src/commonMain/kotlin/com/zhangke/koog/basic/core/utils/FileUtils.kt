package com.zhangke.koog.basic.core.utils

import java.nio.file.Path

expect fun createBuildFolderPath(subfolder: String? = null): Path
