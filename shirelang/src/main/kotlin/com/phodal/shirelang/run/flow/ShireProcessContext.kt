package com.phodal.shirelang.run.flow

import com.phodal.shirecore.llm.Message
import com.phodal.shirelang.compiler.ShireCompiledResult

data class ShireProcessContext(
    val scriptPath: String,
    val compiledResult: ShireCompiledResult,
    val llmResponse: String,
    val ideOutput: String,
    val messages: MutableList<Message> = mutableListOf(),
    var hadReRun: Boolean = false
)