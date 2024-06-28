package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*

open class FunctionStatementProcessor(myProject: Project, hole: HobbitHole) {
    fun execute(statement: Statement, emptyMap: Any): Any {
        return this.processStatement(statement, emptyMap as Map<String, List<Any>>)
    }

    fun <T : Any> processStatement(
        statement: Statement,
        variables: Map<String, List<T>>,
    ): List<T> {
        val result = mutableListOf<T>()
        variables.forEach { (variableName, elements) ->
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
                                logger<QueryStatementProcessor>().warn("unknown operator: $operator")
                            }
                        }
                    }

                    else -> {
                        logger<QueryStatementProcessor>().warn("unknown statement: $statement")
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