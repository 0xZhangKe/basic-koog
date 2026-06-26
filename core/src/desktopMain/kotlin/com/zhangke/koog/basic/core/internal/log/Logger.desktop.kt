package com.zhangke.koog.basic.core.internal.log

import com.zhangke.koog.basic.core.internal.utils.getProjectFolderPath
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.div

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

internal actual fun logToPlatform(content: String) {
    print(content)
    appendLog(content, appendLineSeparator = false)
}

internal actual fun logLineToPlatform(content: String) {
    println(content)
    appendLog(content, appendLineSeparator = true)
}

@Synchronized
private fun appendLog(content: String, appendLineSeparator: Boolean) {
    val logsFolder = getProjectFolderPath().let { java.nio.file.Paths.get(it) / "logs" }
    Files.createDirectories(logsFolder)

    val logFile = logsFolder / "${LocalDate.now().format(dateFormatter)}.log"
    Files.writeString(
        logFile,
        formatLogContent(content, appendLineSeparator),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND,
    )
}

private fun formatLogContent(content: String, appendLineSeparator: Boolean): String {
    val timestamp = LocalDateTime.now().format(dateTimeFormatter)
    val normalizedContent = content.replace("\r\n", "\n").replace('\r', '\n')
    val formattedContent = normalizedContent
        .split('\n')
        .joinToString(System.lineSeparator()) { line -> "$timestamp $line" }

    return if (appendLineSeparator) {
        formattedContent + System.lineSeparator()
    } else {
        formattedContent
    }
}
