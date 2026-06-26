package com.zhangke.koog.basic.core.agent

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.ToolRegistry
import com.zhangke.koog.basic.core.llm.LlmClient
import com.zhangke.koog.basic.core.llm.model.LlmLocalConfig
import com.zhangke.koog.basic.core.provider.OpenAIModelAgentProvider
import com.zhangke.koog.basic.core.tools.ReadBuiltIdResourcesTool
import com.zhangke.koog.basic.core.tools.RunShellTool
import com.zhangke.koog.basic.core.tools.readBundledDoc

object AgentFactory {

    fun createOpenAIAgent(): AgentClient {
        val provider = OpenAIModelAgentProvider(
            systemPrompt = readBundledDoc("system-prompt.md").content,
            apiKey = LlmLocalConfig.OPENAI_API_KEY,
            toolRegistry = ToolRegistry {
                getTools().forEach(::tool)
            },
        )
        val llmClient = LlmClient(provider)
        return AgentClient(llmClient)
    }

    private fun getTools(): List<SimpleTool<*>> {
        return listOf(ReadBuiltIdResourcesTool, RunShellTool)
    }
}
