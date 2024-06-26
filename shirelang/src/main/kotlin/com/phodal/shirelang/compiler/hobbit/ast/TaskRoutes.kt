package com.phodal.shirelang.compiler.hobbit.ast

data class Condition(
    val expression: FrontMatterType.EXPRESSION?,
)

sealed class Task(open val expression: FrontMatterType.EXPRESSION?) {
    class Default(override val expression: FrontMatterType.EXPRESSION?) : Task(expression)
    class CustomTask(override val expression: FrontMatterType.EXPRESSION?) : Task(expression)
}

data class Case(
    val conditionName: String,
    val task: Task,
)

data class TaskRoutes(
    val conditions: Map<FrontMatterType.STRING, Condition>,
    val cases: List<Case>,
    val defaultTask: Task,
) {
    companion object {
        fun from(expression: FrontMatterType.EXPRESSION): TaskRoutes? {
            return null
        }
    }
}
