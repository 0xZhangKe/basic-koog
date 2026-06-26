package com.zhangke.koog.basic.core.provider

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.dashscope.DashscopeClientSettings
import ai.koog.prompt.executor.clients.dashscope.DashscopeLLMClient
import ai.koog.prompt.executor.clients.dashscope.DashscopeModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import com.zhangke.koog.basic.core.llm.model.LlmLocalConfig
import com.zhangke.koog.basic.core.internal.log.Logger

fun buildQwenAgent(
    systemPrompt: String,
    additionalToolRegistry: ToolRegistry = ToolRegistry.EMPTY,
): GraphAIAgent<String, String> {

    val apiKey = LlmLocalConfig.QWEN_API_KEY
    val baseUrl = "https://dashscope.aliyuncs.com/"
    val client = DashscopeLLMClient(
        apiKey = apiKey,
        settings = DashscopeClientSettings(
            baseUrl = baseUrl,
        )
    )
    return AIAgent(
        promptExecutor = MultiLLMPromptExecutor(client),
        systemPrompt = systemPrompt,
        llmModel = DashscopeModels.QWEN3_MAX,
        temperature = 0.4,
        toolRegistry = ToolRegistry {
            tools(additionalToolRegistry.tools)
        },
        maxIterations = 10,
    ) {
        handleEvents {
            // Handle tool calls
            onToolCallStarting { eventContext ->
                Logger.logLine("Tool called: ${eventContext.toolName} with args ${eventContext.toolArgs}")
            }
        }
    } as GraphAIAgent<String, String>
}
