package com.phodal.shirecore.llm

import com.intellij.json.psi.JsonObject
import com.phodal.shirecore.index.findNumber
import com.phodal.shirecore.index.findString

class LlmConfig(
    val title: String,
    val provider: String = "openai",
    val apiBase: String = "https://api.openai.com/v1/chat/completions",
    val apiKey: String,
    val model: String,
    val temperature: Double = 0.0,
    val maxTokens: Int? = 1024,
    val requestFormat: Map<String, String> = mapOf(),
    val responseFormat: String = "\$.choices[0].delta.content",
    val messageKeys: Map<String, String> = mapOf(),
) {
    companion object {
        fun fromJson(modelConfig: JsonObject): LlmConfig? {
            val title = modelConfig.findString("title") ?: return null
            val provider = modelConfig.findString("provider") ?: "openai"
            val apiBase = modelConfig.findString("apiBase") ?: "https://api.openai.com/v1/chat/completions"

            val apiKey = modelConfig.findString("apiKey") ?: return null
            val model = modelConfig.findString("model") ?: return null

            val temperature = try {
                modelConfig.findNumber("temperature")?.toDouble()
            } catch (e: Exception) {
                null
            }
            val maxTokens = try {
                modelConfig.findNumber("maxTokens")?.toInt()
            } catch (e: Exception) {
                null
            }

            return LlmConfig(
                title = title,
                provider = provider,
                apiBase = apiBase,
                apiKey = apiKey,
                model = model,
                temperature = temperature ?: 0.0,
                maxTokens = maxTokens,
                requestFormat = mapOf(),
                responseFormat = "\$.choices[0].delta.content",
                messageKeys = mapOf(),
            )
        }
    }
}