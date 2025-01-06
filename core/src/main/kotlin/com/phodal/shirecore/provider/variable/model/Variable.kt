package com.phodal.shirecore.provider.variable.model

interface Variable {
    val variableName: String
    val description: String
    var value: Any?
}

data class DebugValue(
    override val variableName: String,
    override var value: Any?,
    override val description: String,
) :Variable