package com.phodal.shire.llm

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shire.settings.ShireSettingsState
import com.phodal.shirecore.llm.LlmProvider
import com.theokanning.openai.client.OpenAiApi
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import com.theokanning.openai.service.OpenAiService.defaultClient
import com.theokanning.openai.service.OpenAiService.defaultObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.Duration

class OpenAIProvider : LlmProvider {
    private val logger: Logger = logger<OpenAIProvider>()
    private val timeout = Duration.ofSeconds(defaultTimeout)
    private val modelName: String
        get() = ShireSettingsState.getInstance().modelName
    private val openAiKey: String
        get() = ShireSettingsState.getInstance().apiToken
    private val maxTokenLength: Int get() = 16 * 1024
    private val messages: MutableList<ChatMessage> = ArrayList()
    private var historyMessageLength: Int = 0

    private val service: OpenAiService
        get() {
            if (openAiKey.isEmpty()) {
                throw IllegalStateException("You LLM server Key is empty")
            }

            var openAiProxy = ShireSettingsState.getInstance().apiHost
            return if (openAiProxy.isEmpty()) {
                OpenAiService(openAiKey, timeout)
            } else {
                if (!openAiProxy.endsWith("/")) {
                    openAiProxy += "/"
                }

                val mapper = defaultObjectMapper()
                val client = defaultClient(openAiKey, timeout)

                val retrofit = Retrofit.Builder()
                    .baseUrl(openAiProxy)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

                val api = retrofit.create(OpenAiApi::class.java)
                OpenAiService(api)
            }
        }

    override fun isApplicable(project: Project): Boolean {
        return openAiKey.isNotEmpty() && modelName.isNotEmpty()
    }

    override fun clearMessage() {
        messages.clear()
        historyMessageLength = 0
    }

    override fun text(promptText: String): String {
        val completionRequest = prepareRequest(promptText, "")

        val completion = service.createChatCompletion(completionRequest)
        val output = completion
            .choices[0].message.content

        return output
    }

    override fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean): Flow<String> {
        if (!keepHistory) {
            clearMessage()
        }

        var output = ""
        val completionRequest = prepareRequest(promptText, systemPrompt)

        return callbackFlow {
            withContext(Dispatchers.IO) {
                service.streamChatCompletion(completionRequest)
                    .doOnError { error ->
                        logger.error("Error in stream", error)
                        trySend(error.message ?: "Error occurs")
                    }
                    .blockingForEach { response ->
                        if (response.choices.isNotEmpty()) {
                            val completion = response.choices[0].message
                            if (completion != null && completion.content != null) {
                                output += completion.content
                                trySend(completion.content)
                            }
                        }
                    }

                if (!keepHistory) {
                    clearMessage()
                }

                close()
            }
        }
    }

    private fun prepareRequest(promptText: String, systemPrompt: String): ChatCompletionRequest? {
        if (systemPrompt.isNotEmpty()) {
            val systemMessage = ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt)
            messages.add(systemMessage)
        }

        val userMessage = ChatMessage(ChatMessageRole.USER.value(), promptText)

        historyMessageLength += promptText.length
        if (historyMessageLength > maxTokenLength) {
            messages.clear()
        }

        messages.add(userMessage)
        logger.info("messages length: ${messages.size}")

        val chatCompletionRequest = ChatCompletionRequest.builder()
            .model(modelName)
            .temperature(0.0)
            .messages(messages)
            .build()

        return chatCompletionRequest
    }
}
