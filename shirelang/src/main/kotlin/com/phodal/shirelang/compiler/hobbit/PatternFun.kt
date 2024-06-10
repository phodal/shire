package com.phodal.shirelang.compiler.hobbit

import com.intellij.openapi.diagnostic.logger

/**
 * PatternFun is a sealed class in Kotlin that represents different pattern processing functions.
 * It has four subclasses: Prompt, Grep, Sort, and Xargs, each representing a specific pattern processing function.
 *
 * @property funcName The name of the pattern processing function.
 *
 * The Prompt class represents a prompt function with a message to display.
 *
 * The Grep class represents a grep function with one or more patterns to search for.
 *
 * The Sort class represents a sort function with one or more arguments for sorting.
 *
 * The Xargs class represents an xargs function with one or more variables to process.
 *
 * The companion object provides a method from(value: FrontMatterType) to create a list of PatternFun objects based on the given FrontMatterType.
 * It handles different types of FrontMatterType and returns the corresponding PatternFun objects.
 *
 * If the FrontMatterType is a STRING, it creates a Prompt object with the value as the message.
 * If the FrontMatterType is a PATTERN, it extracts the processors from the value and returns them as a list of PatternFun objects.
 * If the FrontMatterType is neither STRING nor PATTERN, it logs an error and returns an empty list.
 */
sealed class PatternFun(open val funcName: String) {
    class Prompt(val message: String): PatternFun("prompt")
    class Grep(vararg val patterns: String): PatternFun("grep")
    class Sort(vararg val arguments: String): PatternFun("sort")
    class Xargs(vararg val variables: String): PatternFun("xargs")

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