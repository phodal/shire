package com.phodal.shirecore.llm

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
        fun fromMap(map: Map<String, Any>): LlmConfig {
            return LlmConfig(
                title = map["title"] as String,
                provider = map["provider"] as String,
                apiBase = map["apiBase"] as String,
                apiKey = map["apiKey"] as String,
                model = map["model"] as String,
                temperature = try {
                    (map["temperature"] as String).toDouble()
                } catch (e: Exception) {
                    0.0
                },
                customHeaders = try {
                    map["customHeaders"] as Map<String, String>
                } catch (e: Exception) {
                    mapOf()
                },
                customFields = try {
                    map["customFields"] as Map<String, String>
                } catch (e: Exception) {
                    mapOf()
                },
                messageKeys = try {
                    map["messageKeys"] as Map<String, String>
                } catch (e: Exception) {
                    mapOf()
                },
            )
        }
    }
}