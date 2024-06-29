package com.phodal.shire.llm.model

import kotlinx.serialization.Serializable

enum class ChatRole {
    System,
    Assistant,
    User;

    fun roleName(): String {
        return this.name.lowercase()
    }
}

@Serializable
data class ChatMessage(val role: ChatRole, val content: String)

@Serializable
data class CustomRequest(val chatMessages: List<ChatMessage>)
