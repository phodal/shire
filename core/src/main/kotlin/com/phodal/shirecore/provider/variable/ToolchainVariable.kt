package com.phodal.shirecore.provider.variable

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class ToolchainVariable(val variableName: String, var value: Any? = null) {
    Diff("diff"),

    HistoryCommitMessages("historyCommitExample"),
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun from(variableName: String): ToolchainVariable? {
            return values().firstOrNull { it.variableName == variableName }
        }
    }
}