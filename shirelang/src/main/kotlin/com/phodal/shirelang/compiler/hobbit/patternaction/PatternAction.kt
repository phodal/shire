package com.phodal.shirelang.compiler.hobbit.patternaction

import com.intellij.openapi.diagnostic.logger
import com.phodal.shirelang.compiler.hobbit.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ShirePatternAction

sealed class PatternActionFunc(open val funcName: String) {
    /**
     * Prompt subclass for displaying a message prompt.
     *
     * @property message The message to be displayed.
     */
    class Prompt(val message: String) : PatternActionFunc("prompt")

    /**
     * Grep subclass for searching with one or more patterns.
     *
     * @property patterns The patterns to search for.
     */
    class Grep(vararg val patterns: String) : PatternActionFunc("grep")

    /**
     * Sed subclass for find and replace operations.
     *
     * @property pattern The pattern to search for.
     * @property replacements The string to replace matches with.
     */
    class Sed(val pattern: String, val replacements: String) : PatternActionFunc("sed")

    /**
     * Sort subclass for sorting with one or more arguments.
     *
     * @property arguments The arguments to use for sorting.
     */
    class Sort(vararg val arguments: String) : PatternActionFunc("sort")

    /**
     * Uniq subclass for removing duplicates based on one or more arguments.
     *
     * @property texts The texts to process for uniqueness.
     */
    class Uniq(vararg val texts: String) : PatternActionFunc("uniq")

    /**
     * Head subclass for retrieving the first few lines.
     *
     * @property number The number of lines to retrieve from the start.
     */
    class Head(val number: Number) : PatternActionFunc("head")

    /**
     * Tail subclass for retrieving the last few lines.
     *
     * @property number The number of lines to retrieve from the end.
     */
    class Tail(val number: Number) : PatternActionFunc("tail")

    /**
     * Xargs subclass for processing one or more variables.
     *
     * @property variables The variables to process.
     */
    class Xargs(vararg val variables: String) : PatternActionFunc("xargs")

    /**
     * Print subclass for printing one or more texts.
     *
     * @property texts The texts to be printed.
     */
    class Print(vararg val texts: String) : PatternActionFunc("print")

    /**
     * Cat subclass for concatenating one or more files.
     * Paths can be absolute or relative to the current working directory.
     */
    class Cat(vararg val paths: String) : PatternActionFunc("cat")
}

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
                    val action = value.value as? ShirePatternAction ?: return null
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
