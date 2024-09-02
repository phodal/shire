package com.phodal.shirecore.provider.variable.model.toolchain

import com.phodal.shirecore.provider.variable.model.ToolchainVariable

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class GradleToolchainVariable(
    override val variableName: String,
    override var value: Any? = null,
    override val description: String = "",
) : ToolchainVariable {
    GradleDependencies("projectDependencies", description = "The dependencies of the project"),
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun from(variableName: String): GradleToolchainVariable? {
            return values().firstOrNull { it.variableName == variableName }
        }
    }
}

