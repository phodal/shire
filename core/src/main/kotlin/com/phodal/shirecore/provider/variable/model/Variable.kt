package com.phodal.shirecore.provider.variable.model

import com.phodal.shirecore.provider.variable.model.toolchain.*

interface Variable {
    val variableName: String
    val description: String
    var value: Any?
}

data class DebugValue(
    override val variableName: String,
    override var value: Any?,
    override val description: String,
) : Variable {
    companion object {
        fun description(key: String): String {
            return PsiContextVariable.from(key)?.description
                ?: ContextVariable.from(key)?.description
                ?: SystemInfoVariable.from(key)?.description
                ?: ConditionPsiVariable.from(key)?.description
                /// ...
                ?: DatabaseToolchainVariable.from(key)?.description
                ?: TerminalToolchainVariable.from(key)?.description
                ?: VcsToolchainVariable.from(key)?.description
                ?: GradleToolchainVariable.from(key)?.description
                ?: SonarqubeVariable.from(key)?.description
                ?: "Unknown"
        }
    }
}