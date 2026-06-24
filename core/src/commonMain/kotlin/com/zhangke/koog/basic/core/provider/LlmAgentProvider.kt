package com.zhangke.koog.basic.core.provider

import ai.koog.agents.core.agent.GraphAIAgent

interface LlmAgentProvider {

    val platform: String

    val versions: List<String>

    val agent: GraphAIAgent<String, String>
}
