package com.zhangke.koog.basic.core.internal.http

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.http.decodeURLPart
import com.zhangke.koog.basic.core.internal.log.Logger as CoreInternalLogger

object SharedHttpClientConfig {
    var enableLogging: Boolean = true
}

fun HttpClientConfig<out HttpClientEngineConfig>.installSharedHttpLogging() {
    if (!SharedHttpClientConfig.enableLogging) return

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                CoreInternalLogger.logLine(message.toBinaryResponseSummaryOrSelf())
            }
        }
        level = LogLevel.BODY
    }
}

internal fun String.toBinaryResponseSummaryOrSelf(): String {
    if (!startsWith("RESPONSE:")) return this

    val headers = parseLoggedHeaders()
    val contentDisposition = headers[HttpHeaders.ContentDisposition.lowercase()]
    val contentType = headers[HttpHeaders.ContentType.lowercase()]
    if (!isBinaryResponse(contentType, contentDisposition)) return this

    val url = lineSequence()
        .firstOrNull { it.startsWith("FROM:") }
        ?.substringAfter("FROM:")
        ?.trim()
    val fileName = contentDisposition.extractFileName()
        ?: url.extractFileNameFromUrl()
        ?: "unknown"
    val contentLength = headers[HttpHeaders.ContentLength.lowercase()]?.toLongOrNull()
    val statusLine = lineSequence().first()

    return buildString {
        appendLine(statusLine)
        url?.let { appendLine("FROM: $it") }
        append("BINARY RESPONSE: name=$fileName")
        contentLength?.let { append(", length=$it bytes") }
    }
}

private fun String.parseLoggedHeaders(): Map<String, String> {
    return lineSequence()
        .mapNotNull { line ->
            val header = when {
                line.startsWith("-> ") -> line.removePrefix("-> ")
                line.startsWith("BODY ") -> line.removePrefix("BODY ")
                else -> return@mapNotNull null
            }
            val separatorIndex = header.indexOf(':')
            if (separatorIndex < 0) {
                null
            } else {
                header.substring(0, separatorIndex).trim().lowercase() to
                    header.substring(separatorIndex + 1).trim()
            }
        }
        .toMap()
}

private fun isBinaryResponse(contentType: String?, contentDisposition: String?): Boolean {
    if (contentDisposition?.contains("attachment", ignoreCase = true) == true) return true
    if (contentDisposition.extractFileName() != null) return true

    val mediaType = contentType
        ?.substringBefore(';')
        ?.trim()
        ?.lowercase()
        ?: return false

    if (mediaType.startsWith("text/")) return false
    if (mediaType.endsWith("+json") || mediaType.endsWith("+xml")) return false

    return when (mediaType) {
        "application/json",
        "application/xml",
        "application/javascript",
        "application/x-javascript",
        "application/x-www-form-urlencoded",
        "application/graphql",
        "application/graphql-response+json",
        "application/problem+json",
        "application/problem+xml",
        "application/ld+json",
        "application/manifest+json",
        "application/x-ndjson",
        "application/sse",
        "application/event-stream" -> false

        else -> true
    }
}

private fun String?.extractFileName(): String? {
    if (this.isNullOrBlank()) return null

    parameterValue("filename*")?.let { encoded ->
        return encoded
            .substringAfter("''", encoded)
            .trim()
            .trim('"')
            .decodeUrlOrSelf()
            .ifBlank { null }
    }

    return parameterValue("filename")
        ?.trim()
        ?.trim('"')
        ?.decodeUrlOrSelf()
        ?.ifBlank { null }
}

private fun String.parameterValue(name: String): String? {
    return split(';')
        .map { it.trim() }
        .firstOrNull { it.startsWith("$name=", ignoreCase = true) }
        ?.substringAfter('=')
}

private fun String?.extractFileNameFromUrl(): String? {
    if (this.isNullOrBlank()) return null

    return substringBefore('?')
        .substringBefore('#')
        .substringAfterLast('/')
        .decodeUrlOrSelf()
        .ifBlank { null }
}

private fun String.decodeUrlOrSelf(): String {
    return runCatching { decodeURLPart() }.getOrDefault(this)
}
