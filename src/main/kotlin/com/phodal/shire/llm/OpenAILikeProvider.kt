package com.phodal.shire.llm

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shire.custom.CustomSSEHandler
import com.phodal.shire.custom.appendCustomHeaders
import com.phodal.shire.custom.updateCustomFormat
import com.phodal.shire.settings.ShireSettingsState
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.llm.CustomRequest
import com.phodal.shirecore.llm.LlmProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.time.Duration

class OpenAILikeProvider : CustomSSEHandler(), LlmProvider {
    private val logger: Logger = logger<OpenAILikeProvider>()
    private val timeout = Duration.ofSeconds(defaultTimeout)

    private val modelName: String get() = ShireSettingsState.getInstance().modelName
    private val key: String get() = ShireSettingsState.getInstance().apiToken
    private val url: String get() {
        val apiHost = ShireSettingsState.getInstance().apiHost
        if (apiHost.isEmpty()) {
            return "https://api.openai.com/v1/chat/completions"
        }

        return apiHost
    }

    private val messages: MutableList<ChatMessage> = ArrayList()
    private var historyMessageLength: Int = 0
    private var client = OkHttpClient()

    override val requestFormat: String get() = "{ \"customFields\": {\"model\": $modelName, \"stream\": true} }"
    override val responseFormat: String get() = "\$.choices[0].delta.content"

    override fun isApplicable(project: Project): Boolean {
        return key.isNotEmpty() && modelName.isNotEmpty()
    }

    override fun clearMessage() {
        messages.clear()
        historyMessageLength = 0
    }

    override fun text(promptText: String): String {
        messages += ChatMessage(ChatRole.User, promptText)
        val customRequest = CustomRequest(messages)
        val requestContent = Json.encodeToString<CustomRequest>(customRequest)

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), requestContent)

        logger.info("Requesting form: $requestContent $body")
        val builder = Request.Builder()
        if (key.isNotEmpty()) {
            builder.addHeader("Authorization", "Bearer $key")
        }

        try {
            client = client.newBuilder().readTimeout(timeout).build()

            val request = builder.url(url).post(body).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                logger.error("$response")
                return ""
            }

            return response.body?.string() ?: ""
        } catch (e: IllegalArgumentException) {
            logger.error("Failed to set timeout", e)
            return ""
        }
    }

    override fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean): Flow<String> {
        if (!keepHistory) {
            clearMessage()
        }

        messages += ChatMessage(ChatRole.User, promptText)

        val customRequest = CustomRequest(messages)
        val requestContent = customRequest.updateCustomFormat(requestFormat)

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), requestContent)

        val builder = Request.Builder()
        if (key.isNotEmpty()) {
            builder.addHeader("Authorization", "Bearer $key")
            builder.addHeader("Content-Type", "application/json")
        }
        builder.appendCustomHeaders(requestFormat)

        logger.info("Requesting form: $requestContent $body")

        client = client.newBuilder().readTimeout(timeout).build()
        val call = client.newCall(builder.url(url).post(body).build())

        if (!keepHistory) {
            clearMessage()
        }

        return streamSSE(call, messages)
    }
}
