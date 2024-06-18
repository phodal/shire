package com.phodal.shirelang.compiler.hobbit.patternaction

class VariablePatternActionExecutor {
    companion object {
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
}