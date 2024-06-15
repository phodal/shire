package com.phodal.shirelang.compiler

class SymbolTable {
    private val table: MutableMap<String, VariableInfo> = mutableMapOf()

    fun addVariable(name: String, varType: VariableType, lineDeclared: Int, scope: VariableScope = VariableScope.BuiltIn) {
        if (!table.containsKey(name)) {
            table[name] = VariableInfo(varType, scope, lineDeclared)
        } else {
            throw Exception("Variable $name already declared.")
        }
    }

    fun getVariable(name: String): VariableInfo {
        return table[name] ?: throw Exception("Variable $name not found.")
    }

    fun updateVariable(name: String, newType: VariableType? = null, newScope: VariableScope? = null, newLineDeclared: Int? = null) {
        val variable = table[name] ?: throw Exception("Variable $name not found.")
        val updatedVariable = variable.copy(
            type = newType ?: variable.type,
            scope = newScope ?: variable.scope,
            lineDeclared = newLineDeclared ?: variable.lineDeclared
        )
        table[name] = updatedVariable
    }

    fun removeVariable(name: String) {
        if (table.containsKey(name)) {
            table.remove(name)
        } else {
            throw Exception("Variable $name not found.")
        }
    }

    fun getAllVariables(): Map<String, VariableInfo> {
        return table.toMap()
    }

    data class VariableInfo(
        val type: VariableType,
        val scope: VariableScope,
        val lineDeclared: Int
    )

    enum class VariableType {
        String,
        Boolean,
        Number,
    }

    enum class VariableScope {
        BuiltIn,
        UserDefined
    }
}