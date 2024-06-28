package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

open class FunctionStatementProcessor(override val myProject: Project, val hole: HobbitHole) :
    PatternFuncProcessor(myProject, hole) {
    fun execute(statement: Statement, variableTable: MutableMap<String, Any?>): Any? {
        return when (statement) {
            is Comparison -> {
                val result = executeComparison(statement, variableTable)
                result
            }

            is Processor -> {
                execute(statement.processors, variableTable)
            }

            is MethodCall -> {
                invokeLocalMethodCall(statement, variableTable)
            }

            else -> {
                logger<FunctionStatementProcessor>().warn("unknown stmt: $statement expr: ${statement.display()}")
                null
            }
        }
    }

    fun invokeLocalMethodCall(statement: MethodCall, variableTable: MutableMap<String, Any?>): Any? {
        val objName = statement.objectName.display()
        val methodName = statement.methodName.display()
        val methodArgs = statement.arguments

        if (methodName == "") {
            val firstArg = methodArgs?.get(0)
            when (objName) {
                "jsonpath" -> {
                    val output = (variableTable["output"] ?: "").toString()
                    val arg: String = when(firstArg) {
                        is FrontMatterType.STRING -> (methodArgs[0] as FrontMatterType.STRING).value.toString()
                        else -> firstArg.toString()
                    }
                    val json: String = try {
                        JsonPath.parse(output)?.read<Any>(arg).toString()
                    } catch (e: Exception) {
                        logger<FunctionStatementProcessor>().warn("jsonpath error: $e")
                        return null
                    }

                    return json
                }
                "print" -> {
                    val value = firstArg
                    println(value)
                }

                else -> {
                    logger<FunctionStatementProcessor>().warn("unknown method: $objName")
                }
            }
        }

        return null;
    }

    fun execute(processors: List<PatternActionFunc>, variableTable: MutableMap<String, Any?>): Any? {
        val input: Any = variableTable["output"] ?: ""
        var result: Any = variableTable["output"] ?: ""
        processors.forEach { action ->
            result = patternFunctionExecute(action, result, input)
        }

        return result.toString()
    }

    private fun FunctionStatementProcessor.executeComparison(
        statement: Comparison,
        valueMap: Any,
    ): Boolean {
        val operator = statement.operator
        val left = evaluate(statement.left, valueMap)
        val right = evaluate(statement.right, valueMap)

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