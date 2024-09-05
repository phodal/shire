package com.phodal.shire.httpclient.converter

import com.intellij.httpClient.converters.curl.parser.CurlParser
import com.intellij.httpClient.execution.RestClientRequest
import com.intellij.httpClient.http.request.HttpRequestHeaderFields
import com.intellij.json.psi.*
import com.phodal.shirecore.provider.http.VariableFiller
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object CUrlConverter {
    private fun convert(content: String): RestClientRequest {
        val restClientRequest = CurlParser().parseToRestClientRequest(content)
        return restClientRequest
    }

    fun convert(
        content: String,
        envVars: List<Set<String>> = listOf(),
        processVars: Map<String, String> = mapOf(),
        envObj: JsonObject? = null,
    ): Request {
        val builder = Request.Builder()

        val filledContent = VariableFiller.fillVariables(content, envVars, envObj, processVars)
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
}
