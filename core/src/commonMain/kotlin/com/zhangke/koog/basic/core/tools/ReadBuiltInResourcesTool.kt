package com.zhangke.koog.basic.core.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import com.zhangke.koog.basic.core.internal.json.globalJson
import com.zhangke.koog.basic.core.internal.log.Logger
import kotlinx.serialization.Serializable

object ReadBuiltIdResourcesTool : SimpleTool<ReadBuiltIdResourcesTool.Args>(
    argsType = typeToken<Args>(),
    name = "readBuiltInResources",
    description = "Read a bundled document from the docs classpath resource directory by file name.",
) {

    @Serializable
    data class Args(
        @property:LLMDescription("The document file name under the bundled docs directory.")
        val fileName: String,
    )

    override suspend fun execute(args: Args): String {
        val result = readBundledDoc(args.fileName)
        return globalJson.encodeToString(result).also {
            Logger.logLine("readDoc(fileName=${args.fileName}) result: $it")
        }
    }

    @Serializable
    data class ReadDocResult(
        val fileName: String,
        val content: String,
    )
}

expect fun readBundledDoc(fileName: String): ReadBuiltIdResourcesTool.ReadDocResult
