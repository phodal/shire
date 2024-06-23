package com.phodal.shirelang.compiler.hobbit.ast

import com.intellij.psi.PsiElement

class ShirePsiQueryStatement(
    val from: List<VariableStatement>,
    val where: LogicalExpression,
    val select: List<LogicalExpression>,
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
}

class VariableStatement(
    val variableType: Class<out PsiElement>,
    val value: String,
) {
    override fun toString(): String {
        return "$variableType $value"
    }
}
