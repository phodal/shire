package com.phodal.shire.httpclient.converter

import com.intellij.httpClient.converters.curl.parser.CurlParser
import com.intellij.httpClient.execution.RestClientRequest
import com.intellij.httpClient.http.request.HttpRequestHeaderFields
import com.intellij.openapi.util.TextRange
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object CUrlConverter {
    private fun convert(content: String): RestClientRequest {
        val restClientRequest = CurlParser().parseToRestClientRequest(content)
        return restClientRequest
    }

    fun convert(content: String, variables: List<Set<String>> = listOf()): Request {
        val builder = Request.Builder()

        val request = this.convert(
            content)

        builder.url(request.buildFullUrl())
        request.headers.forEach {
            try {
                builder.header(it.key, it.value)
            } catch (e: IllegalArgumentException) {
                // ignore
            }
        }

        val body = request.textToSend

        val mediaType = request
            .getHeadersValue(HttpRequestHeaderFields.CONTENT_TYPE)
            ?.firstOrNull()
            ?.toMediaTypeOrNull()

        when (request.httpMethod) {
            "GET" -> builder.get()
            "POST" -> builder.post(body.toRequestBody(mediaType))
            "PUT" -> builder.put(body.toRequestBody(mediaType))
            "DELETE" -> builder.delete(body.toRequestBody(mediaType))
            else -> builder.method(request.httpMethod, body.toRequestBody(mediaType))
        }

        return builder.build()
    }

    fun fillVariables(messageBody: String, variables: List<Set<String>>): String {
        val variablesRanges = this.collectVariablesRangesInMessageBody(messageBody)
        if (variablesRanges.isEmpty()) return messageBody

        val result = StringBuilder(messageBody.length)
        var lastVariableRangeEndOffset = 0
        for (variableRange in variablesRanges) {
            result.append(messageBody as CharSequence, lastVariableRangeEndOffset, variableRange.startOffset)
            val variableValue =
                variables.firstOrNull { it.contains(variableRange.substring(messageBody)) }?.firstOrNull()
                    ?: ""

            result.append(variableValue)
            lastVariableRangeEndOffset = variableRange.endOffset
        }
        result.append(messageBody as CharSequence, lastVariableRangeEndOffset, messageBody.length)
        val sb = result.toString()
        return sb
    }

    private fun collectVariablesRangesInMessageBody(body: String): List<TextRange> {
        val ranges = mutableListOf<TextRange>()
        var startIndex = 0

        while (startIndex < body.length) {
            val openBraceIndex = body.indexOf("\${", startIndex)
            val closeBraceIndex = body.indexOf("}", openBraceIndex)

            if (openBraceIndex == -1 || closeBraceIndex == -1) {
                break
            }

            val range = TextRange(openBraceIndex, closeBraceIndex + 1)
            val contentInsideBraces = body.substring(openBraceIndex + 2, closeBraceIndex)

            if (contentInsideBraces.isNotBlank()) {
                ranges.add(range)
            }

            startIndex = closeBraceIndex + 1
        }

        return ranges
    }
}