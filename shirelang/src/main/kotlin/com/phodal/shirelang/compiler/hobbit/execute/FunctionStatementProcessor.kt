package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import kotlinx.coroutines.runBlocking

/**
 * The `FunctionStatementProcessor` class is responsible for processing function statements within a project context.
 * It extends the `PatternFuncProcessor` class, which is part of a larger framework likely dealing with pattern matching and processing within a domain-specific language or a scripting environment.
 *
 * This class operates on statements that can be comparisons, processor invocations, or method calls, and it manages a variable table to keep track of variable values during execution.
 *
 * @property myProject The project in which the processing occurs, extending the functionality of the base class `PatternFuncProcessor`.
 * @property hole The hobbit hole, which seems to be a metaphorical representation of a context or a scope, also extending the base class functionality.
 *
 * ### Methods:
 *
 * - `execute(statement: Statement, variableTable: MutableMap<String, Any?>): Any?`
 *   Executes the provided statement within the given variable table context. It supports different types of statements and processes them accordingly using a `runBlocking` coroutine scope to handle asynchronous operations.
 *
 * - `invokeLocalMethodCall(statement: MethodCall, variableTable: MutableMap<String, Any?>): Any?`
 *   Invokes a local method call represented by the given `MethodCall` object, extracting the object name, method name, and arguments. It handles special method calls like "jsonpath" and "print".
 *
 * - `execute(processors: List<PatternActionFunc>, variableTable: MutableMap<String, Any?>): Any?`
 *   Suspends the coroutine to execute a list of processors with the provided input and variable table, applying each processor sequentially to the result.
 *
 * - `executeComparison(statement: Comparison, value: Any): Boolean`
 *   Executes a comparison operation based on the given comparison statement and value, returning a boolean result.
 *
 * - `processStatement(statement: Statement, variableElementsMap: Map<String, List<T>>): List<T>`
 *   Processes a statement against a map of variable names to lists of elements, filtering the elements based on the statement's criteria.
 *
 * - `evaluate(type: FrontMatterType, element: T): Any?`
 *   Evaluates a front matter type against an element, handling various types like arrays, expressions, booleans, dates, identifiers, numbers, and strings.
 *
 * - `evalExpression(type: FrontMatterType, element: T): Any?`
 *   Evaluates an expression front matter type by invoking the appropriate method or field on the given element.
 *
 * - `invokeMethodOrField(methodCall: MethodCall, element: T): Any?`
 *   Invokes a method or field on an element based on the provided method call, which is used to interact with the properties or methods of the element.
 *
 * This class uses the Kotlin `runBlocking` coroutine scope to handle asynchronous operations and may throw an `IllegalArgumentException` for unknown types during evaluation.
 */
open class FunctionStatementProcessor(override val myProject: Project, override val hole: HobbitHole) :
    PatternFuncProcessor(myProject, hole) {
    fun execute(statement: Statement, variableTable: MutableMap<String, Any?>): Any? = runBlocking {
        return@runBlocking when (statement) {
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
                    val arg: String = when (firstArg) {
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

        return null
    }

    suspend fun execute(processors: List<PatternActionFunc>, variableTable: MutableMap<String, Any?>): Any? {
        val input: Any = variableTable["output"] ?: ""
        var result: Any = variableTable["output"] ?: ""

        processors.forEach { action ->
            result = patternFunctionExecute(action, result, input, variableTable)
            variableTable["output"] = result
        }

        return result.toString()
    }

    private fun FunctionStatementProcessor.executeComparison(
        statement: Comparison,
        value: Any,
    ): Boolean {
        val operator = statement.operator
        val left = evaluate(statement.left, value)
        val right = evaluate(statement.right, value)

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

    inline fun <reified T : Any> processStatement(
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

                    is MethodCall -> {
                        when (val output = invokeMethodOrField(statement, element)) {
                            is Collection<*> -> {
                                output.forEach {
                                    if (it is T) {
                                        result.add(it)
                                    }
                                }
                            }

                            is T -> {
                                result.add(output)
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
        val objName = methodCall.objectName.display()
        when (element) {
            is Map<*, *> -> {
                val variable = element[objName] as? String ?: return null
                return methodCall.evaluateExpression(methodCall.methodName, listOf(), variable)
            }

            else -> return null
        }
    }
}