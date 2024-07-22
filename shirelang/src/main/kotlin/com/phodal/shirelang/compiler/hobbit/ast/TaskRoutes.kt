package com.phodal.shirelang.compiler.hobbit.ast

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
    /**
     * A placeholder for the default task
     */
    val defaultTask: Task? = null,
) {
    fun execute(
        myProject: Project,
        context: PostCodeHandleContext,
        hobbitHole: HobbitHole,
    ): List<Case> {
        val conditionResult = mutableMapOf<String, Any?>()
        val variableTable = mutableMapOf<String, Any?>()

        variableTable["output"] = context.genText

        val processor = FunctionStatementProcessor(myProject, hobbitHole)
        conditions.forEach {
            val statement = it.valueExpression.value as Statement
            val result = processor.execute(statement, variableTable)
            conditionResult[it.conditionKey] = result
        }

        val matchedCase = cases.filter {
            val caseKey = it.caseKey

            when (val condValue = conditionResult[caseKey]) {
                is Boolean -> {
                    condValue == true || condValue == "true"
                }

                is String -> {
                    condValue.isNotEmpty()
                }

                else -> {
                    false
                }
            }
        }

        if (matchedCase.isEmpty()) {
            ((defaultTask as? Task.Default)?.expression?.value as? Statement)?.let {
                processor.execute(it, variableTable)
            }

            return emptyList()
        }

        matchedCase.forEach {
            val statement = (it.valueExpression as Task.CustomTask).expression?.value as Statement
            processor.execute(statement, variableTable)
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
                    transformConditionCasesToRoutes(caseExpr.value as ConditionCase)
                }

            return taskRoutes.firstOrNull()
        }

        /**
         * Transforms a given [ConditionCase] into a [TaskRoutes] object which contains a structured set of conditions and corresponding tasks.
         *
         * @param conditionCase The [ConditionCase] object to transform. This object contains conditions and cases that determine routing logic.
         * @return A [TaskRoutes] object that encapsulates the transformed conditions and cases, along with a default task if specified.
         */
        fun transformConditionCasesToRoutes(conditionCase: ConditionCase): TaskRoutes {
            val conditions: List<Condition> = conditionCase.conditions.map {
                val caseKeyValue = it.value as CaseKeyValue

                Condition(caseKeyValue.key.display(), caseKeyValue.value as FrontMatterType.EXPRESSION)
            }

            var defaultTask: Task? = null

            val cases: List<Case> = conditionCase.cases.map {
                val caseKeyValue = it.value as CaseKeyValue
                val caseKey = caseKeyValue.key.display()

                val case = Case(
                    caseKey,
                    Task.CustomTask(caseKeyValue.value as FrontMatterType.EXPRESSION)
                )

                if (caseKey == "default") {
                    defaultTask = Task.Default(caseKeyValue.value as FrontMatterType.EXPRESSION)
                }


                case
            }

            return TaskRoutes(
                conditions = conditions,
                cases = cases,
                defaultTask = defaultTask
            )
        }
    }
}
