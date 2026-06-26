package com.zhangke.koog.basic.core.tools

actual fun readBundledDoc(fileName: String): ReadBuiltIdResourcesTool.ReadDocResult {
    require(fileName.isNotBlank()) { "Document fileName must not be blank." }
    require('/' !in fileName && '\\' !in fileName) { "Document fileName must not contain path separators." }

    val resourcePath = "docs/$fileName"
    val classLoader = Thread.currentThread().contextClassLoader ?: ReadBuiltIdResourcesTool::class.java.classLoader
    val content = classLoader
        .getResourceAsStream(resourcePath)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Bundled document not found: $resourcePath")

    return ReadBuiltIdResourcesTool.ReadDocResult(
        fileName = fileName,
        content = content,
    )
}
