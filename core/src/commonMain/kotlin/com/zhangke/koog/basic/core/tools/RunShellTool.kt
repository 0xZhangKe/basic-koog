package com.zhangke.koog.basic.core.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import com.zhangke.koog.basic.core.internal.json.globalJson
import com.zhangke.koog.basic.core.internal.log.Logger
import kotlinx.serialization.Serializable

object RunShellTool : SimpleTool<RunShellTool.Args>(
    argsType = typeToken<Args>(),
    name = "runShell",
    description = "Run an arbitrary shell command provided by the LLM and return stdout, stderr, exit code, and timeout information.",
) {

    @Serializable
    data class Args(
        @property:LLMDescription("The shell command to execute.")
        val command: String,
        @property:LLMDescription("Optional working directory for the command. When blank, the current process working directory is used.")
        val workingDirectory: String = "",
        @property:LLMDescription("Maximum time to wait for the command in milliseconds. Values less than 1 are treated as 1.")
        val timeoutMillis: Long = 100_000,
        @property:LLMDescription("Maximum number of characters to return for stdout and stderr separately. Values less than 0 are treated as 0.")
        val maxOutputChars: Int = Int.MAX_VALUE,
    )

    override suspend fun execute(args: Args): String {
        val timeoutMillis = args.timeoutMillis.coerceAtLeast(1)
        val maxOutputChars = args.maxOutputChars.coerceAtLeast(0)
        val result = runShellCommand(
            command = args.command,
            workingDirectory = args.workingDirectory,
            timeoutMillis = timeoutMillis,
            maxOutputChars = maxOutputChars,
        )

        return globalJson.encodeToString(result).also {
            Logger.logLine("runShell(command=${args.command}, workingDirectory=${args.workingDirectory}, timeoutMillis=$timeoutMillis, maxOutputChars=$maxOutputChars) result: $it")
        }
    }

    @Serializable
    data class RunShellResult(
        val command: String,
        val workingDirectory: String,
        val exitCode: Int?,
        val stdout: String,
        val stderr: String,
        val stdoutTruncated: Boolean,
        val stderrTruncated: Boolean,
        val timedOut: Boolean,
    )
}

internal expect fun runShellCommand(
    command: String,
    workingDirectory: String,
    timeoutMillis: Long,
    maxOutputChars: Int,
): RunShellTool.RunShellResult
