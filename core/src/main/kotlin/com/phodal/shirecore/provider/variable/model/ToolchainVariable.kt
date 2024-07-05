package com.phodal.shirecore.provider.variable.model

interface ToolchainVariable {
    val variableName: String;
    var value: Any?;
    val description: String;

    companion object {
        fun from(variableName: String): ToolchainVariable? {
            return VcsToolchainVariable.from(variableName) ?: TerminalVariable.from(variableName)
        }
    }
}