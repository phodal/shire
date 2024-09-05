package com.phodal.shire.httpclient.converter

import com.intellij.httpClient.converters.curl.parser.CurlParser
import com.intellij.httpClient.execution.RestClientRequest
import com.intellij.httpClient.http.request.HttpRequestHeaderFields
import com.intellij.json.JsonUtil
import com.intellij.json.psi.*
import com.intellij.openapi.util.TextRange
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object CUrlConverter {
    private fun convert(content: String): RestClientRequest {
        val restClientRequest = CurlParser().parseToRestClientRequest(content)
        return restClientRequest
    }

    fun readEnvObject(psiFile: JsonFile?, envName: String): JsonObject? {
        val rootObject = psiFile?.topLevelValue as? JsonObject ?: return null

        val properties: List<JsonProperty> = rootObject.propertyList
        val envObject = properties.firstOrNull { it.name == envName }?.value as? JsonObject
        return envObject
    }

    fun convert(
        content: String,
        envVars: List<Set<String>> = listOf(),
        processVars: Map<String, String> = mapOf(),
        envObj: JsonObject? = null,
    ): Request {
        val builder = Request.Builder()

        val filledContent = fillVariables(content, envVars, envObj, processVars)
        val request = this.convert(filledContent)

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

    private fun getVariableValue(myVariables: JsonObject, name: String, processVars: Map<String, String>): String? {
        val value = JsonUtil.getPropertyValueOfType(myVariables, name, JsonLiteral::class.java)
        val jsonResult = getValueAsString(value)
        if (jsonResult != null) {
            return jsonResult
        }

        return processVars[name]
    }

    private fun getValueAsString(value: JsonLiteral?): String? {
        return when (value) {
            is JsonStringLiteral -> value.value
            is JsonBooleanLiteral -> value.value.toString()
            else -> value?.text
        }
    }

    fun fillVariables(
        messageBody: String,
        variables: List<Set<String>>,
        obj: JsonObject?,
        processVars: Map<String, String>
    ): String {
        if (obj == null) return messageBody
        if (variables.isEmpty()) return messageBody

        val envRanges = this.collectVariablesRangesInMessageBody(messageBody)

        val result = StringBuilder(messageBody.length)
        var lastVariableRangeEndOffset = 0

        for (variableRange in envRanges) {
            result.append(messageBody as CharSequence, lastVariableRangeEndOffset, variableRange.startOffset)
            val variableValue = getVariableValue(obj, getVariableKey(variableRange, messageBody), processVars)

            result.append(variableValue)
            lastVariableRangeEndOffset = variableRange.endOffset
        }

        result.append(messageBody as CharSequence, lastVariableRangeEndOffset, messageBody.length)
        val sb = result.toString()
        return sb
    }

    private fun getVariableKey(variableRange: TextRange, messageBody: String) =
        variableRange.substring(messageBody).removePrefix("\${").removeSuffix("}")

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
