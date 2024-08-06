package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import com.phodal.shirecore.vcs.ShireGitCommit
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.*
import com.phodal.shirelang.compiler.hobbit.execute.model.ShireQLVariableBuilder
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.patternaction.PatternActionTransform
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
 * This class uses the Kotlin `runBlocking` coroutine scope to handle asynchronous operations and may throw an `IllegalArgumentException` for unknown types during evaluation.
 */
open class FunctionStatementProcessor(override val myProject: Project, override val hole: HobbitHole) :
    PatternFuncProcessor(myProject, hole) {
    inline fun <reified T : Any> execute(expression: Statement, variables: Map<String, List<T>>): List<T> {
        return processStatement(expression, variables)
    }

    open fun execute(transform: PatternActionTransform): String {
        val fromStmt = transform.patternActionFuncs.find { it is PatternActionFunc.From } as PatternActionFunc.From
        val selectStmt =
            transform.patternActionFuncs.find { it is PatternActionFunc.Select } as PatternActionFunc.Select
        val whereStmt = transform.patternActionFuncs.find { it is PatternActionFunc.Where } as PatternActionFunc.Where

        val variableElementsMap: Map<String, List<Any>> = runReadAction {
            ShireQLVariableBuilder(myProject, hole).buildVariables(fromStmt)
        }
        val handledElements = processStatement(whereStmt.statement, variableElementsMap)
        val selectElements = processSelect(selectStmt, handledElements)

        return selectElements.joinToString("\n")
    }

    fun processSelect(selectStmt: PatternActionFunc.Select, handledElements: List<Any>): List<String> {
        return selectStmt.statements.flatMap {
            processSelectStatement(it, handledElements)
        }
    }

    private fun processSelectStatement(statement: Statement, handledElements: List<Any>): List<String> {
        val result = mutableListOf<String>()
        handledElements.forEach { element ->
            when (element) {
                is PsiElement -> {
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

                is ShireGitCommit -> {
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
            }
        }

        return result
    }

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

        var lastOutput: Any? = result

        processors.forEach { action ->
            result = patternFunctionExecute(action, result, input, variableTable)

            if (action.funcName == "execute") {
                if (lastOutput != null) {
                    result = lastOutput as Any
                }
            }

            lastOutput = result
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
//
//                    is LogicalExpression -> {
//                        processLogic(statement, variableElementsMap, result)
//                    }

                    else -> {
                        logger<FunctionStatementProcessor>().warn("unknown statement: ${statement.display()}")
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