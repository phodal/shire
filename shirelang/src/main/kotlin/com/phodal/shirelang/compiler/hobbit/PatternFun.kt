package com.phodal.shirelang.compiler.hobbit

import com.intellij.openapi.diagnostic.logger

/**
 * PatternFun is a sealed class in Kotlin that represents different pattern processing functions.
 * It has four subclasses: Prompt, Grep, Sort, and Xargs, each representing a specific pattern processing function.
 *
 * @property funcName The name of the pattern processing function.
 */
sealed class PatternFun(open val funcName: String) {
    class Prompt(val message: String) : PatternFun("prompt")

    /**
     * Like `grep` function with one or more patterns to search for.
     */
    class Grep(vararg val patterns: String) : PatternFun("grep")

    /**
     * Find and replace function with one or more patterns to search for and replace with.
     */
    class Sed(val pattern: String, val replacements: String) : PatternFun("sed")

    /**
     * Sort function with one or more arguments for sorting.
     */
    class Sort(vararg val arguments: String) : PatternFun("sort")

    /**
     * uniq function with one or more arguments for removing duplicates.
     */
    class Uniq(vararg val texts: String) : PatternFun("uniq")

    /**
     * head function with one or more arguments for getting the first lines.
     */
    class Head(val lines: Number) : PatternFun("head")

    /**
     * tail function with one or more arguments for getting the last lines.
     */
    class Tail(val lines: Number) : PatternFun("tail")

    /**
     * Xargs function with one or more variables to process.
     */
    class Xargs(vararg val variables: String) : PatternFun("xargs")

    companion object {
        fun from(value: FrontMatterType): List<PatternFun> {
            return when (value) {
                is FrontMatterType.STRING -> {
                    return listOf(Prompt(value.value as? String ?: ""))
                }

                is FrontMatterType.PATTERN -> {
                    val action = value.value as? ShirePatternAction
                    action?.processors ?: emptyList()
                }

                else -> {
                    logger<PatternFun>().error("Unknown pattern processor type: $value")
                    emptyList()
                }
            }
        }
    }
}