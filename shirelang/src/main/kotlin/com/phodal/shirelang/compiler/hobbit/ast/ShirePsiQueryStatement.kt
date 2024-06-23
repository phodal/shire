package com.phodal.shirelang.compiler.hobbit.ast

class ShirePsiQueryStatement(
    val from: List<VariableStatement>,
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
}

class VariableStatement(
//    private val variableType: Class<out PsiElement>,
    val variableType: String,
    val value: String,
) {
    override fun toString(): String {
        return "$variableType $value"
    }
}
