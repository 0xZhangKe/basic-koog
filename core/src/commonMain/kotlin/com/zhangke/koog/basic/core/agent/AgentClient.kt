package com.zhangke.koog.basic.core.agent

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.koog.basic.core.llm.LlmClient
import kotlinx.coroutines.flow.Flow

class AgentClient(
    private val llmClient: LlmClient,
) {

    suspend fun send(
        text: String,
        imagePaths: List<String> = emptyList(),
    ): String {
        return llmClient.send(text, imagePaths)
    }

    fun sendStreaming(
        text: String,
        imagePaths: List<String> = emptyList(),
    ): Flow<StreamFrame> {
        return llmClient.sendStreaming(text, imagePaths)
    }
}
