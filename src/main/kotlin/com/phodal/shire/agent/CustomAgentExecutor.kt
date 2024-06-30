package com.phodal.shire.agent

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shire.custom.CustomRequest
import com.phodal.shire.custom.CustomSSEProcessor
import com.phodal.shire.custom.Message
import com.phodal.shire.custom.updateCustomFormat
import com.phodal.shirecore.agent.AuthType
import com.phodal.shirecore.agent.CustomAgent
import com.phodal.shirecore.agent.CustomAgentResponseAction
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Service(Service.Level.PROJECT)
class CustomAgentExecutor(val project: Project) : CustomSSEProcessor(project) {
    private var client = OkHttpClient()
    private val logger = logger<CustomAgentExecutor>()
    private val messages: MutableList<Message> = mutableListOf()

    override var requestFormat: String = ""
    override var responseFormat: String = ""

    fun execute(promptText: String, agent: CustomAgent): Flow<String>? {
        messages.add(Message("user", promptText))

        this.requestFormat = agent.connector?.requestFormat ?: this.requestFormat
        this.responseFormat = agent.connector?.responseFormat ?: this.responseFormat

        val customRequest = CustomRequest(listOf(Message("user", promptText)))
        val request = if (requestFormat.isNotEmpty()) {
            customRequest.updateCustomFormat(requestFormat)
        } else {
            Json.encodeToString<CustomRequest>(customRequest)
        }

        val body = request.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val builder = Request.Builder()

        val auth = agent.auth
        when (auth?.type) {
            AuthType.Bearer -> {
                builder.addHeader("Authorization", "Bearer ${auth.token}")
                builder.addHeader("Content-Type", "application/json")
            }
            null -> {
                logger.info("No auth type found for agent ${agent.name}")
            }
        }

        client = client.newBuilder().connectTimeout(agent.defaultTimeout, TimeUnit.SECONDS).readTimeout(agent.defaultTimeout,
            TimeUnit.SECONDS
        ).build()
        val call = client.newCall(builder.url(agent.url).post(body).build())

        return when (agent.responseAction) {
            CustomAgentResponseAction.Stream -> {
                streamSSE(call, messages = messages)
            }

            else -> {
                streamJson(call, messages)
            }
        }
    }
}