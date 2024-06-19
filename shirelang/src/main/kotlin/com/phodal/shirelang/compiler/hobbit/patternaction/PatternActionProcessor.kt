package com.phodal.shirelang.compiler.hobbit.patternaction

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.hobbit.HobbitHole

class PatternActionProcessor(val myProject: Project, val editor: Editor, val hole: HobbitHole) {
    /**
     * We should execute the variable function with the given key and pipeline functions.
     *
     * Each function output will be the input of the next function.
     */
    fun execute(actionTransform: PatternActionTransform): String {
        // get pipeline pattern data ine here
        return this.execute(actionTransform, "")
    }

    fun execute(transform: PatternActionTransform, input: Any): String {
        var result = input
        transform.patternActionFuncs.forEach { action ->
            when (action) {
                is PatternActionFunc.Prompt -> {
                    result = action.message
                }

                is PatternActionFunc.Grep -> {
                    result = (result as String).split("\n").filter { line -> action.patterns.any { line.contains(it) } }
                        .joinToString("\n")
                }

                is PatternActionFunc.Sed -> {
                    result = (result as String).replace(action.pattern.toRegex(), action.replacements)
                }

                is PatternActionFunc.Sort -> {
                    result = (result as String).split("\n").sorted().joinToString("\n")
                }

                is PatternActionFunc.Uniq -> {
                    result = (result as String).split("\n").distinct().joinToString("\n")
                }

                is PatternActionFunc.Head -> {
                    result = (result as String).split("\n").take(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Tail -> {
                    result = (result as String).split("\n").takeLast(action.number.toInt()).joinToString("\n")
                }

                is PatternActionFunc.Cat -> {
                    result = action.paths.joinToString("\n")
                }

                is PatternActionFunc.Print -> {
                    result = action.texts.joinToString("\n")
                }

                is PatternActionFunc.Xargs -> {
                    result = action.variables
                }
            }
        }

        return result.toString()
    }

}