package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*
import java.util.*

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
        val result = mutableListOf<Any>()
        variables.forEach { (variableName, elements) ->
            elements.forEach { element ->
                when (whereStmt) {
                    is Comparison -> {
                        val operator = whereStmt.operator
                        val left = evaluate(whereStmt.left, element)
                        val right = evaluate(whereStmt.right, element)

                        when (operator.type) {
                            OperatorType.Equal -> {
                                if (left != null && left == right) {
                                    result.add(element)
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
                    }

                    is MethodCall -> TODO()
                    is NotExpression -> TODO()
                    is StringComparison -> TODO()
                    is StringOperatorStatement -> TODO()
                    is Value -> TODO()

                    else -> {
                        logger<QueryStatementProcessor>().warn("unknown statement: $whereStmt")
                    }
                }
            }
        }

        return result
    }

    private fun evaluate(type: FrontMatterType, element: PsiElement): Any? {
        return when (type) {
            is FrontMatterType.ARRAY -> TODO()
            is FrontMatterType.BOOLEAN -> TODO()
            is FrontMatterType.DATE -> TODO()
            is FrontMatterType.EXPRESSION -> {
                when (type.value) {
                    is MethodCall -> {
                        val methodCall = type.value
                        val methodName = methodCall.methodName.display()
                        val methodArgs = methodCall.arguments

                        val isField = methodArgs == null

                        if (isField) {
                            val field = element.javaClass.fields.find {
                                it.name == methodName
                            }

                            if (field != null) {
                                return field.get(element)
                            }
                        }

                        // use reflection to call method
                        val method = element.javaClass.methods.find {
                            it.name == methodName
                        }
                        if (method != null) {
                            return method.invoke(element, methodArgs)
                        }

                        if (isField) {
                            // maybe getter, we try to find getter, first upper case method name first letter
                            val getterName = "get${
                                methodName.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                }
                            }"
                            val getter = element.javaClass.methods.find {
                                it.name == getterName
                            }

                            if (getter != null) {
                                return getter.invoke(element)
                            }
                        }

                        logger<QueryStatementProcessor>().warn("method or field not found: $methodName")
                        return null
                    }

                    else -> {
                        logger<QueryStatementProcessor>().warn("unknown expression: ${type.value}")
                        return null
                    }
                }
            }

            is FrontMatterType.IDENTIFIER -> TODO()
            is FrontMatterType.NUMBER -> TODO()
            is FrontMatterType.OBJECT -> TODO()
            is FrontMatterType.STRING -> {
                type.value
            }

            is FrontMatterType.VARIABLE -> TODO()
            else -> {
                throw IllegalArgumentException("unknown type: $type")
            }
        }
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): String {
        return "select"
    }
}
