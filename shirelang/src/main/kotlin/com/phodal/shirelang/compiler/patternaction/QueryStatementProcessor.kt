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
        val selectElements = processSelect(selectStmt, handledElements)

        return selectElements.joinToString("\n")
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
    ): List<PsiElement> {
        val result = mutableListOf<PsiElement>()
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

                            OperatorType.And -> {
                                if (left != null && left == right) {
                                    result.add(element)
                                }
                            }

                            OperatorType.GreaterEqual -> {
                                if (left as Comparable<Any> >= right as Comparable<Any>) {
                                    result.add(element)
                                }
                            }

                            OperatorType.GreaterThan -> {
                                if (left as Comparable<Any> > right as Comparable<Any>) {
                                    result.add(element)
                                }
                            }

                            OperatorType.LessEqual -> {
                                if (left as Comparable<Any> <= right as Comparable<Any>) {
                                    result.add(element)
                                }
                            }

                            OperatorType.LessThan -> {
                                if (left as Comparable<Any> < right as Comparable<Any>) {
                                    result.add(element)
                                }
                            }

                            OperatorType.NotEqual -> {
                                if (left != null && left != right) {
                                    result.add(element)
                                }
                            }

                            OperatorType.Or -> {
                                if (left == true || right == true) {
                                    result.add(element)
                                }
                            }

                            else -> {
                                logger<QueryStatementProcessor>().warn("unknown operator: $operator")
                            }
                        }
                    }

                    else -> {
                        logger<QueryStatementProcessor>().warn("unknown statement: $whereStmt")
                    }
                }
            }
        }

        return result
    }

    private fun evalExpression(type: FrontMatterType, element: PsiElement): Any? {
        when (type.value) {
            is MethodCall -> {
                return invokeMethodOrField(type.value, element)
            }

            else -> {
                throw IllegalArgumentException("unknown type: $type")
            }
        }
    }

    private fun invokeMethodOrField(methodCall: MethodCall, element: PsiElement): Any? {
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
        val allMethods = element.javaClass.methods
        val method = allMethods.find {
            it.name == methodName
        }
        if (method != null) {
            if (methodArgs == null) {
                return method.invoke(element)
            }

            return method.invoke(element, methodArgs)
        }

        if (isField) {
            // maybe getter, we try to find getter, first upper case method name first letter
            val getterName = "get${
                methodName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }"
            val getter = allMethods.find {
                it.name == getterName
            }

            if (getter != null) {
                return getter.invoke(element)
            }
        }

        val supportMethodNames: List<String> = allMethods.map {
            it.name
        }
        val supportFieldNames: List<String> = element.javaClass.fields.map {
            it.name
        }

        logger<QueryStatementProcessor>().error("method or field not found: $methodName" +
                "\nsupported methods: $supportMethodNames" +
                "\nsupported fields: $supportFieldNames")
        return null
    }

    private fun evaluate(type: FrontMatterType, element: PsiElement): Any? {
        return when (type) {
            is FrontMatterType.ARRAY -> {
                (type.value as List<FrontMatterType>).map {
                    evaluate(it, element)
                }
            }

            is FrontMatterType.EXPRESSION -> {
                evalExpression(type, element)
            }

            is FrontMatterType.BOOLEAN,
            is FrontMatterType.DATE,
            is FrontMatterType.IDENTIFIER,
            is FrontMatterType.NUMBER,
            is FrontMatterType.STRING,
            -> {
                type.value
            }

            else -> {
                throw IllegalArgumentException("unknown type: $type")
            }
        }
    }

    private fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<PsiElement>): List<String> {
        return selectStmt.statements.flatMap {
            processSelectStatement(it, handledElements)
        }
    }

    private fun processSelectStatement(statement: Statement, handledElements: List<PsiElement>): List<String> {
        val result = mutableListOf<String>()
        handledElements.forEach { element ->
            when (statement) {
                is Value -> {
                    result.add(statement.display())
                }

                is MethodCall -> {
                    invokeMethodOrField(statement, element)?.let {
                        result.add(it.toString())
                    }
                }
            }
        }

        return result
    }
}
