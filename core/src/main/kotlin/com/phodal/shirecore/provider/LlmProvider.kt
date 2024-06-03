package com.phodal.shirecore.provider

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.ShirelangNotifications
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface LlmProvider {
    val defaultTimeout: Long get() = 600

    fun isApplicable(project: Project): Boolean {
        return false
    }

    fun prompt(promptText: String): String

    fun stream(promptText: String, systemPrompt: String, keepHistory: Boolean = true): Flow<String> {
        return callbackFlow {
            val prompt = prompt(promptText)
            trySend(prompt)

            awaitClose()
        }
    }

    fun clearMessage() {

    }

    fun appendLocalMessage(msg: String, role: ChatRole) {}

    companion object {
        private val EP_NAME: ExtensionPointName<LlmProvider> =
            ExtensionPointName.create("com.phodal.shireLlmProvider")

        fun obtain(project: Project): LlmProvider? {
            val providers = EP_NAME.extensions.filter { it.isApplicable(project) }
            if (providers.isEmpty()) {
                ShirelangNotifications.notify(project, "No LLM provider found")
                return null
            }

            return providers.first()
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