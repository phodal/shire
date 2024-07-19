package com.phodal.shirecore.llm

import kotlinx.serialization.Serializable

enum class ChatRole {
    system,
    assistant,
    user;
}

@Serializable
data class ChatMessage(val role: ChatRole, val content: String)

@Serializable
data class CustomRequest(val messages: List<ChatMessage>)
