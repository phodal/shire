package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*

open class FunctionStatementProcessor(myProject: Project, hole: HobbitHole) {
    fun execute(statement: Statement, variableTable: MutableMap<String, Any?>): Any? {
        return when (statement) {
            is Comparison -> {
                val result = executeComparison(statement, variableTable)
                result
            }

            else -> {
                logger<FunctionStatementProcessor>().warn("unknown stmt: $statement expr: ${statement.display()}")
                null
            }
        }
    }

    private fun FunctionStatementProcessor.executeComparison(
        statement: Comparison,
        emptyMap: Any,
    ): Boolean {
        val operator = statement.operator
        val left = evaluate(statement.left, emptyMap)
        val right = evaluate(statement.right, emptyMap)

        return when (operator.type) {
            OperatorType.Equal -> {
                left == right
            }

            OperatorType.And -> {
                left == right
            }

            OperatorType.GreaterEqual -> {
                 if (left == null || right == null) {
                     false
                 } else {
                     left as Comparable<Any> >= right as Comparable<Any>
                 }
            }

            OperatorType.GreaterThan -> {
                if (left == null || right == null) {
                    false
                } else {
                    left as Comparable<Any> > right as Comparable<Any>
                }
            }

            OperatorType.LessEqual -> {
                if (left == null || right == null) {
                    false
                } else {
                    left as Comparable<Any> <= right as Comparable<Any>
                }
            }

            OperatorType.LessThan -> {
                if (left == null || right == null) {
                    false
                } else {
                    left as Comparable<Any> < right as Comparable<Any>
                }
            }

            OperatorType.NotEqual -> {
                left != right
            }

            OperatorType.Or -> {
                left == true || right == true
            }

            else -> {
                logger<FunctionStatementProcessor>().warn("unknown operator: $operator")
                false
            }
        }
    }

    fun <T : Any> processStatement(
        statement: Statement,
        variableElementsMap: Map<String, List<T>>,
    ): List<T> {
        val result = mutableListOf<T>()
        variableElementsMap.forEach { (variableName, elements) ->
            elements.forEach { element ->
                when (statement) {
                    is Comparison -> {
                        val operator = statement.operator
                        val left = evaluate(statement.left, element)
                        val right = evaluate(statement.right, element)

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
                                logger<FunctionStatementProcessor>().warn("unknown operator: $operator")
                            }
                        }
                    }

                    else -> {
                        logger<FunctionStatementProcessor>().warn("unknown statement: $statement")
                    }
                }
            }
        }

        return result
    }

    fun <T : Any> evaluate(type: FrontMatterType, element: T): Any? {
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

    open fun <T : Any> evalExpression(type: FrontMatterType, element: T): Any? {
        when (type.value) {
            is MethodCall -> {
                return invokeMethodOrField(type.value, element)
            }

            else -> {
                throw IllegalArgumentException("unknown type: $type")
            }
        }
    }

    open fun <T : Any> invokeMethodOrField(methodCall: MethodCall, element: T): Any? {
        //
        return null
    }
}