package com.zhangke.koog.basic.framework.log

object Logger {

    fun log(content: String) {
        logToPlatform(content)
    }

    fun logLine(content: String) {
        logLineToPlatform(content)
    }
}

internal expect fun logToPlatform(content: String)

internal expect fun logLineToPlatform(content: String)
