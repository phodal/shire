package com.phodal.shirelang.compiler

class SymbolTable {
    private val table: MutableMap<String, VariableInfo> = mutableMapOf()

    fun addVariable(name: String, varType: String, scope: VariableScope, lineDeclared: Int) {
        if (!table.containsKey(name)) {
            table[name] = VariableInfo(varType, scope, lineDeclared)
        } else {
            throw Exception("Variable $name already declared.")
        }
    }

    fun getVariable(name: String): VariableInfo {
        return table[name] ?: throw Exception("Variable $name not found.")
    }

    fun updateVariable(name: String, newType: String? = null, newScope: VariableScope? = null, newLineDeclared: Int? = null) {
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

    data class VariableInfo(
        val type: String,
        val scope: VariableScope,
        val lineDeclared: Int
    )

    enum class VariableScope {
        BuiltIn,
        UserDefined
    }
}