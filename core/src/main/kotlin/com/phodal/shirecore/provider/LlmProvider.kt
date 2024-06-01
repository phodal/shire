package com.phodal.shirecore.provider

import com.intellij.openapi.project.Project
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface LlmProvider {
    val defaultTimeout: Long get() = 600

    fun prompt(promptText: String): String

    @OptIn(ExperimentalCoroutinesApi::class)
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
        // todo: implement
        fun create(project: Project): LlmProvider {
            return object : LlmProvider {
                override fun prompt(promptText: String): String {
                    return ""
                }
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