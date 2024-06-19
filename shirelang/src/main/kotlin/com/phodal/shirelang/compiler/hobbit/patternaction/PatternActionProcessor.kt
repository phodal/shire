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
        return actionTransform.execute("")
    }
}