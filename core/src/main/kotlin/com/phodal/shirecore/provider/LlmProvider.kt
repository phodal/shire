package com.phodal.shirecore.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import kotlinx.coroutines.flow.Flow

interface LlmProvider {
    val defaultTimeout: Long get() = 600

    fun isApplicable(project: Project): Boolean

    fun prompt(promptText: String): String

    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true): Flow<String>

    fun clearMessage()

    companion object {
        val EP_NAME: ExtensionPointName<LlmProvider> =
            ExtensionPointName.create("com.phodal.shireLlmProvider")

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
