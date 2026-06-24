package com.zhangke.koog.basic.core.llm.utils

import kotlin.math.ceil

class TokenEstimator(
    private val latinCharsPerToken: Double = 4.0,
    private val symbolCharsPerToken: Double = 2.0,
) {

    fun estimate(text: String): Int {
        if (text.isBlank()) {
            return 0
        }

        var cjkTokenCount = 0
        var latinCharCount = 0
        var symbolCharCount = 0

        text.forEach { char ->
            when {
                char.isWhitespace() -> Unit
                char.isCjkChar() -> cjkTokenCount += 1
                char.isLetterOrDigit() || char.isAsciiPunctuation() -> latinCharCount += 1
                else -> symbolCharCount += 1
            }
        }

        return cjkTokenCount +
                latinCharCount.toEstimatedTokenCount(latinCharsPerToken) +
                symbolCharCount.toEstimatedTokenCount(symbolCharsPerToken)
    }

    private fun Int.toEstimatedTokenCount(charsPerToken: Double): Int {
        if (this == 0) {
            return 0
        }
        return ceil(this / charsPerToken).toInt()
    }
}

private fun Char.isCjkChar(): Boolean {
    return code in 0x3400..0x4DBF ||
            code in 0x4E00..0x9FFF ||
            code in 0xF900..0xFAFF ||
            code in 0x3040..0x30FF ||
            code in 0xAC00..0xD7AF
}

private fun Char.isAsciiPunctuation(): Boolean {
    return code in 0x21..0x2F ||
            code in 0x3A..0x40 ||
            code in 0x5B..0x60 ||
            code in 0x7B..0x7E
}
