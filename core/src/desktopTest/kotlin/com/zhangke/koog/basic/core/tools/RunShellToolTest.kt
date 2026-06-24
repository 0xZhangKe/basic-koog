package com.zhangke.koog.basic.core.tools

import com.zhangke.koog.basic.framework.json.globalJson
import kotlinx.coroutines.runBlocking
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RunShellToolTest {

    @Test
    fun execute_runsCommandAndReturnsStdout() = runBlocking {
        val rawResult = RunShellTool.execute(
            RunShellTool.Args(
                command = "echo hello",
            )
        )
        val result = globalJson.decodeFromString<RunShellTool.RunShellResult>(rawResult)

        assertEquals(0, result.exitCode)
        assertEquals("hello", result.stdout.trim())
        assertEquals("", result.stderr)
        assertFalse(result.stdoutTruncated)
        assertFalse(result.stderrTruncated)
        assertFalse(result.timedOut)
    }

    @Test
    fun execute_usesWorkingDirectory() = runBlocking {
        val root = createTempDirectory(prefix = "run-shell-tool-cwd-")

        val rawResult = RunShellTool.execute(
            RunShellTool.Args(
                command = "pwd",
                workingDirectory = root.toString(),
            )
        )
        val result = globalJson.decodeFromString<RunShellTool.RunShellResult>(rawResult)
        val expectedWorkingDirectory = root.toFile().canonicalPath

        assertEquals(0, result.exitCode)
        assertEquals(expectedWorkingDirectory, result.stdout.trim())
        assertEquals(expectedWorkingDirectory, result.workingDirectory)
    }

    @Test
    fun execute_truncatesStdout() = runBlocking {
        val rawResult = RunShellTool.execute(
            RunShellTool.Args(
                command = "printf 1234567890",
                maxOutputChars = 4,
            )
        )
        val result = globalJson.decodeFromString<RunShellTool.RunShellResult>(rawResult)

        assertEquals(0, result.exitCode)
        assertEquals("1234", result.stdout)
        assertTrue(result.stdoutTruncated)
    }
}
