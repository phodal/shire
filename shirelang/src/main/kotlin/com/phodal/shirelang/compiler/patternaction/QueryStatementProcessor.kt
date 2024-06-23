package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*

class QueryStatementProcessor(val myProject: Project, editor: Editor, hole: HobbitHole) {
    fun execute(transform: PatternActionTransform): String {
        val fromStmt = transform.patternActionFuncs.find { it is PatternActionFunc.From } as PatternActionFunc.From
        val selectStmt =
            transform.patternActionFuncs.find { it is PatternActionFunc.Select } as PatternActionFunc.Select
        val whereStmt = transform.patternActionFuncs.find { it is PatternActionFunc.Where } as PatternActionFunc.Where

        val variables: Map<String, List<PsiElement>> = buildVariables(fromStmt)
        val handledElements = processCondition(whereStmt.statement, variables)

        return processSelect(selectStmt, handledElements)
    }

    private fun buildVariables(fromStmt: PatternActionFunc.From): Map<String, List<PsiElement>> {
        return fromStmt.variables.associate {
            it.value to lookupElement(it)
        }
    }

    // cache
    private val cache = mutableMapOf<String, List<PsiElement>>()

    private fun lookupElement(it: VariableElement): List<PsiElement> {
        if (cache.containsKey(it.variableType)) {
            return cache[it.variableType] ?: emptyList()
        }

        val elements: List<PsiElement> = ShireSymbolProvider.all().flatMap { provider ->
            provider.lookupElementByName(myProject, it.variableType) ?: emptyList()
        }

        cache[it.variableType] = elements
        return elements
    }

    private fun processCondition(
        whereStmt: Statement,
        variables: Map<String, List<PsiElement>>,
    ): List<Any> {
        return when (whereStmt) {
            is Comparison -> {
                val operator = whereStmt.operator
                when(operator.type) {
                    OperatorType.Equal -> {
                        // compare value
                        val value = whereStmt.variable

                    }
                    OperatorType.And -> TODO()
                    OperatorType.GreaterEqual -> TODO()
                    OperatorType.GreaterThan -> TODO()
                    OperatorType.LessEqual -> TODO()
                    OperatorType.LessThan -> TODO()
                    OperatorType.Not -> TODO()
                    OperatorType.NotEqual -> TODO()
                    OperatorType.Or -> TODO()
                }

                emptyList()
            }

            is LogicalExpression -> {
                emptyList()
            }

            is MethodCall -> {
                val value = whereStmt.objectName.display()
                val variables = variables[value]  ?: return emptyList()
                variables.map {
                    // use reflection to call method
//                    it.
                }
            }

            is NotExpression -> {
                emptyList()
            }

            is StringComparison -> {
                emptyList()
            }

            is StringOperatorStatement -> {
                emptyList()
            }

            is Value -> {
                emptyList()
            }

            else -> {
                return emptyList()
            }
        }
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): String {
        return "select"
    }
}
