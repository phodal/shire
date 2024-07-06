package com.phodal.shirecore.provider.variable.model

interface Variable {
    val variableName: String
    val description: String
    var value: Any?
}