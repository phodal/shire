package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
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
        return variables.flatMap { (variableName, elements) ->
            when (whereStmt) {
                is Comparison -> {
                    val operator = whereStmt.operator
                    val left = evaluate(whereStmt.left, variableName, elements)
                    val right = evaluate(whereStmt.right, variableName, elements)

                    when(operator.type) {
                        OperatorType.Equal -> {
                            if (left == right) {
                                return listOf(left)
                            }
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

                is MethodCall -> {
                    val value = whereStmt.objectName.display()
                    val variables = variables[value]  ?: return emptyList()
                    variables.map {
                        // use reflection to call method
//                    it.
                    }
                }

                is NotExpression -> TODO()
                is StringComparison -> TODO()
                is StringOperatorStatement -> TODO()
                is Value -> TODO()

                else -> {
                    return emptyList()
                }
            }
        }
    }

    private fun evaluate(left: FrontMatterType, variableName: String, elements: List<PsiElement>): Any {
        return when (left) {
            is FrontMatterType.ARRAY -> TODO()
            is FrontMatterType.BOOLEAN -> TODO()
            is FrontMatterType.CASE_MATCH -> TODO()
            is FrontMatterType.DATE -> TODO()
            is FrontMatterType.ERROR -> TODO()
            is FrontMatterType.EXPRESSION -> {
                // TODO
            }
            is FrontMatterType.IDENTIFIER -> TODO()
            is FrontMatterType.NUMBER -> TODO()
            is FrontMatterType.OBJECT -> TODO()
            is FrontMatterType.PATTERN -> TODO()
            is FrontMatterType.QUERY_STATEMENT -> TODO()
            is FrontMatterType.STRING -> {
                return left.display()
            }
            is FrontMatterType.VARIABLE -> TODO()
        }
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): String {
        return "select"
    }
}
