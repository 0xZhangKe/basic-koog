package com.zhangke.koog.basic.core.provider

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.agent.GraphAIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import com.zhangke.koog.basic.core.internal.log.Logger

class OpenAIModelAgentProvider(
    private val systemPrompt: String,
    private val apiKey: String,
    private val toolRegistry: ToolRegistry = ToolRegistry.EMPTY,
) : LlmAgentProvider {

    override val platform: String = "OpenAI"

    override val versions: List<String> = listOf("GPT-5.5")

    override val agent: GraphAIAgent<String, String> by lazy {
        buildOpenAIAgent(systemPrompt, toolRegistry)
    }

    private fun buildOpenAIAgent(
        systemPrompt: String,
        additionalToolRegistry: ToolRegistry = ToolRegistry.EMPTY,
    ): GraphAIAgent<String, String> {
        val client = OpenAILLMClient(apiKey = apiKey)

        return AIAgent(
            promptExecutor = MultiLLMPromptExecutor(client),
            systemPrompt = systemPrompt,
            llmModel = LLModel(
                provider = LLMProvider.OpenAI,
                id = "gpt-5.5",
                capabilities = listOf(
                    LLMCapability.Completion,
                    LLMCapability.Speculation,
                    LLMCapability.Tools,
                    LLMCapability.ToolChoice,
                    LLMCapability.Vision.Image,
                    LLMCapability.Document,
                    LLMCapability.MultipleChoices,
                    LLMCapability.OpenAIEndpoint.Completions,
                    LLMCapability.OpenAIEndpoint.Responses,
                    LLMCapability.Schema.JSON.Basic,
                    LLMCapability.Schema.JSON.Standard,
                    LLMCapability.Thinking,
                ),
                contextLength = 1_050_000,
                maxOutputTokens = 128_000,
            ),
            temperature = 1.0,
            toolRegistry = ToolRegistry {
                tools(additionalToolRegistry.tools)
            },
            maxIterations = 30,
        ) {
            handleEvents {
                onToolCallStarting { eventContext ->
                    Logger.logLine("Tool called: ${eventContext.toolName} with args ${eventContext.toolArgs}")
                }
            }
        } as GraphAIAgent<String, String>
    }
}
