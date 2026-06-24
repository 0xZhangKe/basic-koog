package com.zhangke.koog.basic.core.agent

import ai.koog.agents.core.tools.ToolRegistry
import com.zhangke.koog.basic.core.llm.LlmClient
import com.zhangke.koog.basic.core.llm.model.LlmLocalConfig
import com.zhangke.koog.basic.core.provider.OpenAIModelAgentProvider
import com.zhangke.koog.basic.core.tools.ReadDocTool
import com.zhangke.koog.basic.core.tools.RunShellTool
import com.zhangke.koog.basic.core.tools.readBundledDoc

object AgentFactory {

    fun createOpenAIAgent(): AgentClient {
        val provider = OpenAIModelAgentProvider(
            systemPrompt = readBundledDoc("system-prompt.md").content,
            apiKey = LlmLocalConfig.OPENAI_API_KEY,
            toolRegistry = ToolRegistry {
                tool(ReadDocTool)
                tool(RunShellTool)
            },
        )
        val llmClient = LlmClient(provider)
        return AgentClient(llmClient)
    }
}
