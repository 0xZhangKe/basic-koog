package com.zhangke.koog.basic.core.llm.utils

import ai.koog.prompt.streaming.StreamFrame
import com.zhangke.koog.basic.framework.utils.ifEmptyOrBlank

val StreamFrame.message: String
    get() = when (this) {
        is StreamFrame.TextDelta -> this.text
        is StreamFrame.ReasoningDelta -> this.summary?.ifEmptyOrBlank { this.text.orEmpty() } ?: this.text.orEmpty()
        is StreamFrame.ToolCallDelta -> "Tool calling: ${this.name}"
        is StreamFrame.ToolCallComplete -> "${this.name} call completed."
        is StreamFrame.TextComplete -> this.text
        is StreamFrame.ReasoningComplete -> this.finalSummary?.ifEmptyOrBlank { this.finalText } ?: this.finalText
        is StreamFrame.End -> this.finishReason.orEmpty()
    }

val StreamFrame.ReasoningComplete.finalText: String
    get() = this.text.joinToString("")

val StreamFrame.ReasoningComplete.finalSummary: String?
    get() = this.summary?.joinToString("")
