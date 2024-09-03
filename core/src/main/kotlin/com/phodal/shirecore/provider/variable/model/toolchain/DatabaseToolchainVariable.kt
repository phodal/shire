package com.phodal.shirecore.provider.variable.model.toolchain

import com.phodal.shirecore.provider.variable.model.ToolchainVariable

enum class DatabaseToolchainVariable(
    override val variableName: String,
    override var value: Any? = null,
    override val description: String = "",
) : ToolchainVariable {
    DatabaseInfo("databaseInfo", description = "The database information"),
    Databases("databases", description = "The databases in the database"),
    Tables("tables", description = "The tables in the database"),
    Columns("columns", description = "The columns in the database")
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun from(variableName: String): DatabaseToolchainVariable? {
            return DatabaseToolchainVariable.values().firstOrNull { it.variableName == variableName }
        }
    }
}