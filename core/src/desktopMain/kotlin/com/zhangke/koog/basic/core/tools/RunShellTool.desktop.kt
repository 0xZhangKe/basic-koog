package com.zhangke.koog.basic.core.tools

import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

internal actual fun runShellCommand(
    command: String,
    workingDirectory: String,
    timeoutMillis: Long,
    maxOutputChars: Int,
): RunShellTool.RunShellResult {
    require(command.isNotBlank()) { "Command must not be blank." }
    require(timeoutMillis >= 1) { "timeoutMillis must be at least 1." }
    require(maxOutputChars >= 0) { "maxOutputChars must be at least 0." }

    val workingDir = workingDirectory
        .takeIf { it.isNotBlank() }
        ?.let(::File)
        ?: File(System.getProperty("user.dir"))
    require(workingDir.isDirectory) { "Working directory does not exist or is not a directory: ${workingDir.absolutePath}" }
    val canonicalWorkingDir = workingDir.canonicalFile

    val process = ProcessBuilder(shellCommand(command))
        .directory(canonicalWorkingDir)
        .start()
    val stdoutCapture = AsyncStreamCapture(process.inputStream, maxOutputChars)
    val stderrCapture = AsyncStreamCapture(process.errorStream, maxOutputChars)
    stdoutCapture.start()
    stderrCapture.start()

    val completed = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)
    if (!completed) {
        process.destroyForcibly()
        process.waitFor()
    }

    val stdout = stdoutCapture.await()
    val stderr = stderrCapture.await()

    return RunShellTool.RunShellResult(
        command = command,
        workingDirectory = canonicalWorkingDir.absolutePath,
        exitCode = if (completed) process.exitValue() else null,
        stdout = stdout.content,
        stderr = stderr.content,
        stdoutTruncated = stdout.truncated,
        stderrTruncated = stderr.truncated,
        timedOut = !completed,
    ).also {
        println("Executed command: ${it.command} in ${canonicalWorkingDir.absolutePath}.")
    }
}

private fun shellCommand(command: String): List<String> {
    val osName = System.getProperty("os.name").lowercase()
    return if (osName.contains("windows")) {
        listOf("cmd.exe", "/C", command)
    } else {
        listOf("/bin/sh", "-c", command)
    }
}

private class AsyncStreamCapture(
    private val inputStream: InputStream,
    private val maxChars: Int,
) {
    private var result: StreamCaptureResult? = null
    private var failure: Throwable? = null
    private val worker = thread(start = false, name = "run-shell-stream-capture") {
        try {
            result = captureStream(inputStream, maxChars)
        } catch (throwable: Throwable) {
            failure = throwable
        }
    }

    fun start() {
        worker.start()
    }

    fun await(): StreamCaptureResult {
        worker.join()
        failure?.let { throw it }
        return result ?: StreamCaptureResult(content = "", truncated = false)
    }
}

private data class StreamCaptureResult(
    val content: String,
    val truncated: Boolean,
)

private fun captureStream(inputStream: InputStream, maxChars: Int): StreamCaptureResult {
    val content = StringBuilder()
    var truncated = false
    InputStreamReader(inputStream).use { reader ->
        val buffer = CharArray(4096)
        while (true) {
            val readCount = reader.read(buffer)
            if (readCount == -1) {
                break
            }
            val remaining = maxChars - content.length
            if (remaining > 0) {
                content.appendRange(buffer, 0, readCount.coerceAtMost(remaining))
            }
            if (readCount > remaining) {
                truncated = true
            }
        }
    }
    return StreamCaptureResult(
        content = content.toString(),
        truncated = truncated,
    )
}
