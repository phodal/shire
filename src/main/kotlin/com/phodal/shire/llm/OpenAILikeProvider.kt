package com.phodal.shire.llm

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiManager
import com.intellij.psi.search.ProjectScope
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shire.custom.CustomSSEHandler
import com.phodal.shire.custom.appendCustomHeaders
import com.phodal.shire.custom.updateCustomFormat
import com.phodal.shire.settings.ShireSettingsState
import com.phodal.shirecore.index.SHIRE_ENV_ID
import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirecore.llm.ChatRole
import com.phodal.shirecore.llm.CustomRequest
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.middleware.ShireRunVariableContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Duration

class OpenAILikeProvider : CustomSSEHandler(), LlmProvider {
    private val logger: Logger = logger<OpenAILikeProvider>()
    private val timeout = Duration.ofSeconds(defaultTimeout)

    private var modelName: String = ShireSettingsState.getInstance().modelName
    private var temperature: Float  = ShireSettingsState.getInstance().temperature
    private var key: String = ShireSettingsState.getInstance().apiToken
    private var url: String = ShireSettingsState.getInstance().apiHost.ifEmpty {
        "https://api.openai.com/v1/chat/completions"
    }

    private val messages: MutableList<ChatMessage> = ArrayList()
    private var client = OkHttpClient()

    override val requestFormat: String get() = """{ "customFields": {"model": "$modelName", "temperature": $temperature, "stream": true} }"""
    override val responseFormat: String get() = "\$.choices[0].delta.content"

    override fun isApplicable(project: Project): Boolean {
        return key.isNotEmpty() && modelName.isNotEmpty()
    }

    override fun clearMessage() {
        messages.clear()
    }

    override fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean): Flow<String> {
//        configRunLlm()

        if (!keepHistory) {
            clearMessage()
        }

        messages += ChatMessage(ChatRole.user, promptText)

        val customRequest = CustomRequest(messages)
        val requestContent = customRequest.updateCustomFormat(requestFormat)

        val body = requestContent.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

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
