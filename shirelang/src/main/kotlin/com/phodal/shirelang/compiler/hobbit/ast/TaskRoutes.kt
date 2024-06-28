package com.phodal.shirelang.compiler.hobbit.ast

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.execute.FunctionStatementProcessor


data class TaskRoutesContext(
    /**
     * the LLM output
     */
    val output: String,
)

data class Condition(
    val conditionKey: String,
    val valueExpression: FrontMatterType.EXPRESSION,
)

sealed class Task(open val expression: FrontMatterType.EXPRESSION?) {
    class CustomTask(override val expression: FrontMatterType.EXPRESSION?) : Task(expression)
    class Default(override val expression: FrontMatterType.EXPRESSION?) : Task(expression)
}

data class Case(
    val caseKey: String,
    val valueExpression: Task,
)

data class TaskRoutes(
    val conditions: List<Condition>,
    val cases: List<Case>,
    val defaultTask: Task,
) {
    fun execute(myProject: Project, console: ConsoleView?, context: PostCodeHandleContext, hobbitHole: HobbitHole): Case? {
        val conditionResult = mutableMapOf<String, Any?>()

        val processor = FunctionStatementProcessor(myProject, hobbitHole)
        conditions.forEach {
            val statement = it.valueExpression.value as Statement
            val result = processor.execute(statement, conditionResult)
            conditionResult[it.conditionKey] = result
        }

        val matchedCase = cases.find {
            val caseKey = it.caseKey
            val caseValue = it.valueExpression

            val caseStatement = caseValue.expression?.value as Statement
            val result = processor.execute(caseStatement, conditionResult)

            conditionResult[caseKey] == result
        }

        return matchedCase
    }

    companion object {
        fun from(expression: FrontMatterType.ARRAY): TaskRoutes? {
            val arrays = expression.value as List<FrontMatterType>
            val taskRoutes = arrays.filterIsInstance<FrontMatterType.EXPRESSION>()
                .filter {
                    it.value is ConditionCase
                }
                .map { caseExpr ->
                    val conditionCase = caseExpr.value as ConditionCase
                    val conditions: List<Condition> = conditionCase.conditions.map {
                        val caseKeyValue = it.value as CaseKeyValue

                        Condition(
                            caseKeyValue.key.display(),
                            caseKeyValue.value as FrontMatterType.EXPRESSION
                        )
                    }

                    val cases: List<Case> = conditionCase.cases.map {
                        val caseKeyValue = it.value as CaseKeyValue
                        Case(
                            caseKeyValue.key.display(),
                            Task.Default(caseKeyValue.value as FrontMatterType.EXPRESSION)
                        )
                    }

                    TaskRoutes(
                        conditions = conditions,
                        cases = cases,
                        defaultTask = Task.Default(null)
                    )
                }

            return taskRoutes.firstOrNull()
        }
    }
}
