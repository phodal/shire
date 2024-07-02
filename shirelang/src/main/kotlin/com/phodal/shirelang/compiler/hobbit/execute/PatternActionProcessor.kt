package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.patternaction.PatternActionTransform


class PatternActionProcessor(override val myProject: Project, override val hole: HobbitHole) :
PatternFuncProcessor(myProject, hole) {
    /**
     * We should execute the variable function with the given key and pipeline functions.
     *
     * Each function output will be the input of the next function.
     */
    fun execute(actionTransform: PatternActionTransform): String {
        if (actionTransform.patternActionFuncs.isEmpty()) {
            return ""
        }

        if (actionTransform.isQueryStatement) {
            return QueryStatementProcessor(myProject, hole).execute(actionTransform)
        }

        var input: Any = ""
        // todo: update rules for input type
        if (actionTransform.pattern.isNotBlank()) {
            input = PatternSearcher.findFilesByRegex(myProject, actionTransform.pattern)
                .map { it.path }
                .toTypedArray()
        }

        return this.execute(actionTransform, input)
    }

    /**
     * This method is used to execute a series of transformations on the input based on the provided PatternActionTransform.
     * The transformations are applied in the order they are defined in the PatternActionTransform.
     * The input can be of any type, but the transformations are applied as if the input is a String.
     * If the input is not a String, it will be converted to a String before applying the transformations.
     * The result of each transformation is used as the input for the next transformation.
     * If the transformation is a Cat, the executeCatFunc method is called with the action and the original input.
     * The result of the last transformation is returned as a String.
     *
     * @param transform The PatternActionTransform that defines the transformations to be applied.
     * @param input The input on which the transformations are to be applied.
     * @return The result of applying the transformations to the input as a String.
     */
    fun execute(transform: PatternActionTransform, input: Any): String {
        var result = input
        transform.patternActionFuncs.forEach { action ->
            result = patternFunctionExecute(action, result, input)
        }

        return result.toString()
    }
}
