package com.phodal.shirecore.llm

import com.intellij.json.psi.JsonObject

class LlmConfig(
    val title: String,
    val provider: String = "openai",
    val apiBase: String = "https://api.openai.com/v1/chat/completions",
    val apiKey: String,
    val model: String,
    val temperature: Double = 0.5,
    val customHeaders: Map<String, String> = mapOf(),
    val customFields: Map<String, String> = mapOf(),
    val messageKeys: Map<String, String> = mapOf(),
) {
    companion object {
        fun fromJson(modelConfig: JsonObject): LlmConfig? {
            val title = modelConfig.findProperty("title")?.value?.text ?: return null
            val provider = modelConfig.findProperty("provider")?.value?.text ?: "openai"
            val apiBase = modelConfig.findProperty("apiBase")?.value?.text
                ?: "https://api.openai.com/v1/chat/completions"

            val apiKey = modelConfig.findProperty("apiKey")?.value?.text ?: return null
            val model = modelConfig.findProperty("model")?.value?.text ?: return null
            val temperature = try {
                modelConfig.findProperty("temperature")?.value?.text?.toDouble() ?: 0.5
            } catch (e: Exception) {
                0.5
            }

            return LlmConfig(
                title = title,
                provider = provider,
                apiBase = apiBase,
                apiKey = apiKey,
                model = model,
                temperature = temperature,
                customHeaders = mapOf(),
                customFields = mapOf(),
                messageKeys = mapOf(),
            )
        }
    }
}