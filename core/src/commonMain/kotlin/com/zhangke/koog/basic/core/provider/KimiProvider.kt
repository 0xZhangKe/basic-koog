package com.zhangke.koog.basic.core.provider

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.openai.OpenAIClientSettings
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.zhangke.koog.basic.core.llm.model.LlmLocalConfig
import com.zhangke.koog.basic.framework.log.Logger

fun buildKimiAgent(
    systemPrompt: String,
    additionalToolRegistry: ToolRegistry = ToolRegistry.EMPTY,
): GraphAIAgent<String, String> {
    val apiKey = LlmLocalConfig.KIMI_API_KEY
    val baseUrl = "https://api.moonshot.ai"
    val client = OpenAILLMClient(
        apiKey = apiKey,
        settings = OpenAIClientSettings(
            baseUrl = baseUrl,
            chatCompletionsPath = "v1/chat/completions",
            modelsPath = "v1/models",
        )
    )

    return AIAgent(
        promptExecutor = MultiLLMPromptExecutor(client),
        systemPrompt = systemPrompt,
        llmModel = KimiModels.K2_6,
        temperature = 1.0,
        toolRegistry = ToolRegistry {
            tools(additionalToolRegistry.tools)
        },
        maxIterations = 10,
    ) {
        handleEvents {
            onToolCallStarting { eventContext ->
                Logger.logLine("Tool called: ${eventContext.toolName} with args ${eventContext.toolArgs}")
            }
        }
    } as GraphAIAgent<String, String>
}

private object KimiModels {
    val K2_6: LLModel = LLModel(
        provider = LLMProvider.OpenAI,
        id = "kimi-k2.6",
        capabilities = listOf(
            LLMCapability.Temperature,
            LLMCapability.Tools,
            LLMCapability.ToolChoice,
            LLMCapability.Vision.Image,
            LLMCapability.Vision.Video,
            LLMCapability.Completion,
            LLMCapability.MultipleChoices,
            LLMCapability.OpenAIEndpoint.Completions,
        ),
    )
}
