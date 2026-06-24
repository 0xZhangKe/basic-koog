package com.zhangke.koog.basic.framework.utils

inline fun String.ifEmptyOrBlank(defaultValue: () -> String): String {
    if (isEmpty()) return defaultValue()
    if (isBlank()) return defaultValue()
    return this
}
