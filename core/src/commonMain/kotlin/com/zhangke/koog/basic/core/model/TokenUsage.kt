package com.zhangke.koog.basic.core.model

import ai.koog.prompt.streaming.StreamFrame

data class TokenUsage(
    val totalTokensCount: Int? = null,
    val inputTokensCount: Int? = null,
    val outputTokensCount: Int? = null,
) {

    operator fun plus(other: TokenUsage): TokenUsage {
        return TokenUsage(
            totalTokensCount = totalTokensCount.plusNullable(other.totalTokensCount),
            inputTokensCount = inputTokensCount.plusNullable(other.inputTokensCount),
            outputTokensCount = outputTokensCount.plusNullable(other.outputTokensCount),
        )
    }

    companion object {

        fun fromEndFrame(frame: StreamFrame.End): TokenUsage {
            return TokenUsage(
                totalTokensCount = frame.metaInfo.totalTokensCount,
                inputTokensCount = frame.metaInfo.inputTokensCount,
                outputTokensCount = frame.metaInfo.outputTokensCount,
            )
        }
    }
}

private fun Int?.plusNullable(other: Int?): Int? {
    if (this == null && other == null) return null
    return (this ?: 0) + (other ?: 0)
}
