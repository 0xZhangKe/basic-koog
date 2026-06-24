package com.zhangke.koog.basic.framework.http

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedHttpClientConfigTest {

    @Test
    fun binaryResponseWithBodyContentTypeIsSummarized() {
        val log = """
            RESPONSE: 200 OK
            METHOD: HttpMethod(value=GET)
            FROM: https://example.com/files/report.zip
            BODY Content-Type: application/zip
            BODY Content-Length: 2048
            BODY START
            binary data that should not be printed
            BODY END
        """.trimIndent()

        assertEquals(
            """
                RESPONSE: 200 OK
                FROM: https://example.com/files/report.zip
                BINARY RESPONSE: name=report.zip, length=2048 bytes
            """.trimIndent(),
            log.toBinaryResponseSummaryOrSelf(),
        )
    }

    @Test
    fun binaryResponseWithoutContentLengthOmitsLength() {
        val log = """
            RESPONSE: 200 OK
            FROM: https://example.com/files/archive.bin
            BODY Content-Type: application/octet-stream
            BODY START
            binary data that should not be printed
            BODY END
        """.trimIndent()

        assertEquals(
            """
                RESPONSE: 200 OK
                FROM: https://example.com/files/archive.bin
                BINARY RESPONSE: name=archive.bin
            """.trimIndent(),
            log.toBinaryResponseSummaryOrSelf(),
        )
    }

    @Test
    fun textResponseIsUnchanged() {
        val log = """
            RESPONSE: 200 OK
            BODY Content-Type: text/plain
            BODY START
            hello
            BODY END
        """.trimIndent()

        assertEquals(log, log.toBinaryResponseSummaryOrSelf())
    }
}
