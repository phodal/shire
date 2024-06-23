package com.phodal.shirelang.compiler.hobbit.ast

import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

class ShirePsiQueryStatement(
    val from: List<VariableElement>,
    val where: Statement,
    val select: List<Statement>,
) {
    override fun toString(): String {
        return """
            from {
                ${from.joinToString(", ")} 
            }
            where {
                $where
            } 
            select ${select.joinToString(", ")}"""
            .trimIndent()
    }

    fun toPatternActionFunc(): List<PatternActionFunc> {
        val selectFunc = PatternActionFunc.From(from)
        val whereFunc = PatternActionFunc.Where(where)
        val selectFuncs = PatternActionFunc.Select(select)
        return listOf(selectFunc, whereFunc, selectFuncs)
    }
}

class VariableElement(
    private val variableType: String,
    private val value: String,
) {
    override fun toString(): String {
        return "$variableType $value"
    }
}
