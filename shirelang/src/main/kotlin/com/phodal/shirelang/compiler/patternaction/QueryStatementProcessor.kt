package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*
import org.mozilla.javascript.ast.StringLiteral

class QueryStatementProcessor(val myProject: Project, editor: Editor, hole: HobbitHole) {
    fun execute(transform: PatternActionTransform): String {
        val fromStmt = transform.patternActionFuncs.find { it is PatternActionFunc.From } as PatternActionFunc.From
        val selectStmt =
            transform.patternActionFuncs.find { it is PatternActionFunc.Select } as PatternActionFunc.Select
        val whereStmt = transform.patternActionFuncs.find { it is PatternActionFunc.Where } as PatternActionFunc.Where

        val variables: Map<String, Class<out PsiElement>> = buildVariables(fromStmt)
        val handledElements = processCondition(whereStmt.statement, variables)

        return processSelect(selectStmt, handledElements)
    }

    private fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, Class<out PsiElement>> {
        fromStmt.variables.map {
            when (it.variableType) {
                "PsiClass" -> {
                    //
                }
            }


        }

        return emptyMap()
    }

    private fun processCondition(
        whereStmt: Statement,
        variables: Map<String, Class<out PsiElement>>,
    ): List<Any> {
        when (whereStmt) {
            is Comparison -> {

            }

            is LogicalExpression -> {

            }

            is MethodCall -> {

            }

            is NotExpression -> {

            }

            is StringComparison -> {

            }

            is StringOperatorStatement -> {

            }

            is Value -> {

            }

            else -> {
                return emptyList()
            }
        }

        return emptyList()
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): String {
        return "select"
    }
}
