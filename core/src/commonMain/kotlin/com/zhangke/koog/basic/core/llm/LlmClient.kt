package com.zhangke.koog.basic.core.llm

import ai.koog.agents.core.environment.GenericAgentEnvironment
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.message.Message
import ai.koog.prompt.streaming.StreamFrame
import ai.koog.prompt.streaming.toMessageResponses
import com.zhangke.koog.basic.core.provider.LlmAgentProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.files.Path

class LlmClient(
    private val agent: LlmAgentProvider,
) {

    val platform: String get() = agent.platform

    suspend fun send(
        text: String,
        imagePaths: List<String> = emptyList(),
    ): String {
        val result = executeConversation(
            text = text,
            imagePaths = imagePaths,
        )
        return result
    }

    fun sendStreaming(
        text: String,
        imagePaths: List<String> = emptyList(),
    ): Flow<StreamFrame> = flow {
        executeConversation(
            text = text,
            imagePaths = imagePaths,
            onFrame = ::emit,
        )
    }

    private suspend fun executeConversation(
        text: String,
        imagePaths: List<String> = emptyList(),
        onFrame: suspend (StreamFrame) -> Unit = {},
    ): String {
        val agent = agent.agent
        val config = agent.agentConfig
        val promptExecutor = agent.promptExecutor
        val toolDescriptors = agent.toolRegistry.tools.map { it.descriptor }
        val environment = GenericAgentEnvironment(
            agentId = agent.id,
            logger = logger,
            toolRegistry = agent.toolRegistry,
            serializer = config.serializer,
        )

        var currentPrompt = prompt(config.prompt) {
            user {
                text(text)
                imagePaths.forEach { image(Path(it)) }
            }
        }

        repeat(config.maxAgentIterations) {
            val frames = mutableListOf<StreamFrame>()
            promptExecutor.executeStreaming(
                prompt = currentPrompt,
                model = config.model,
                tools = toolDescriptors,
            ).collect { frame ->
                frames += frame
                onFrame(frame)
            }

            val responses = frames.toMessageResponses()
            currentPrompt = prompt(currentPrompt) {
                messages(responses)
            }

            val toolCalls = responses.filterIsInstance<Message.Tool.Call>()
            if (toolCalls.isEmpty()) {
                return responses
                    .filterIsInstance<Message.Assistant>()
                    .joinToString(separator = "\n") { it.content }
            }
            val toolResults = environment.executeTools(toolCalls)
            currentPrompt = prompt(currentPrompt) {
                messages(toolResults.map { it.toMessage(agent.clock) })
            }
        }

        error("LlmClient exceeded max iterations: ${config.maxAgentIterations}")
    }

    private companion object {
        private val logger = KotlinLogging.logger {}
    }
}
