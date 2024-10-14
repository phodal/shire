package com.phodal.shirelang.run.flow

import com.phodal.shirecore.llm.ChatMessage
import com.phodal.shirelang.compiler.parser.ShireParsedResult

/**
 * The `ShireProcessContext` class represents the context of a Shire process.
 */
data class ShireProcessContext(
    val scriptPath: String,
    val compiledResult: ShireParsedResult,
    val llmResponse: String,
    val ideOutput: String,
    val chatMessages: MutableList<ChatMessage> = mutableListOf(),
    var alreadyReRun: Boolean = false
)