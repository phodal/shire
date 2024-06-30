package com.phodal.shirecore.provider.variable

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class ToolsetVariable(val variableName: String) {
    Diff("diff"),

    HistoryCommitExample("historyCommitExample"),
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun from(variableName: String): ToolsetVariable? {
            return values().firstOrNull { it.variableName == variableName }
        }
    }
}