package com.phodal.shirelang.compiler.hobbit.ast

import com.intellij.openapi.diagnostic.logger
import com.phodal.shirelang.compiler.hobbit.patternaction.PatternActionFunc

/**
 * PatternFun is a sealed class in Kotlin representing different pattern processing functions.
 * It has several subclasses: Prompt, Grep, Sed, Sort, Uniq, Head, Tail, Xargs, and Print,
 * each representing a specific pattern processing function.
 *
 * @property funcName The name of the pattern processing function.
 */
data class PatternAction(
    val pattern: String,
    val patternFuncs: List<PatternActionFunc>,
) {
    companion object {
        /**
         * Creates a list of PatternFun instances from a FrontMatterType object.
         *
         * @param value The FrontMatterType object.
         * @return A list of corresponding PatternFun instances.
         */
        fun from(value: FrontMatterType): PatternAction? {
            return when (value) {
                is FrontMatterType.STRING -> {
                    PatternAction("", listOf(PatternActionFunc.Prompt(value.value as? String ?: "")))
                }

                is FrontMatterType.PATTERN -> {
                    val action = value.value as? RuleBasedPatternAction ?: return null
                    PatternAction(action.pattern, action.processors)
                }

                else -> {
                    logger<PatternAction>().error("Unknown pattern processor type: $value")
                    null
                }
            }
        }
    }
}
