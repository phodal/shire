package com.phodal.shire.llm

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import kotlinx.coroutines.flow.Flow

interface LlmProvider {
    val defaultTimeout: Long get() = 600

    fun isApplicable(project: Project): Boolean

    fun prompt(promptText: String): String

    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true): Flow<String>

    fun clearMessage()

    companion object {
        private val EP_NAME: ExtensionPointName<LlmProvider> =
            ExtensionPointName.create("com.phodal.shireLlmProvider")

        fun provider(project: Project): LlmProvider? {
            val providers = EP_NAME.extensions.filter { it.isApplicable(project) }
            return if (providers.isEmpty()) {
                ShirelangNotifications.notify(project, "No LLM provider found")
                null
            } else {
                providers.first()
            }
        }

        enum class ChatRole {
            System,
            Assistant,
            User;

            fun roleName(): String {
                return this.name.lowercase()
            }
        }
    }
}
