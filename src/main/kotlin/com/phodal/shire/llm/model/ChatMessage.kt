package com.phodal.shire.llm.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(val role: String, val content: String)

@Serializable
data class CustomRequest(val chatMessages: List<ChatMessage>)
