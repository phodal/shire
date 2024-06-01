package com.phodal.shirecore.llm

import kotlinx.serialization.Serializable

@Serializable
data class Message(val role: String, val content: String)

@Serializable
data class CustomRequest(val messages: List<Message>)
