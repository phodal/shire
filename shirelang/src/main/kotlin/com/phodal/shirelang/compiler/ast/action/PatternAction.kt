package com.phodal.shirelang.compiler.ast.action

import com.intellij.openapi.diagnostic.logger
import com.phodal.shirelang.compiler.ast.FrontMatterType
import com.phodal.shirelang.compiler.ast.ShirePsiQueryStatement
import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFunc

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
    val isQueryStatement: Boolean = false
) : DirectAction(patternFuncs) {
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
                    PatternAction("", listOf(PatternActionFunc.Print(value.value as? String ?: "")))
                }

                is FrontMatterType.PATTERN -> {
                    val action = value.value as? RuleBasedPatternAction ?: return null
                    PatternAction(action.pattern, action.processors)
                }

                is FrontMatterType.QUERY_STATEMENT -> {
                    val action = value.value as? ShirePsiQueryStatement ?: return null
                    PatternAction("", action.toPatternActionFunc(), true)
                }

                else -> {
                    logger<PatternAction>().warn("Unknown pattern processor type: ${value.display()}")
                    null
                }
            }
        }
    }
}
