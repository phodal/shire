package com.phodal.shirelang.compiler.patternaction

import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement

/**
 * The `PatternActionFunc` is a sealed class in Kotlin that represents a variety of pattern action functions.
 * Each subclass represents a different function, and each has a unique set of properties relevant to its function.
 *
 * @property funcName The name of the function.
 *
 * @constructor Creates an instance of PatternActionFunc with a function name.
 *
 * @see Prompt
 * @see Grep
 * @see Sed
 * @see Sort
 * @see Uniq
 * @see Head
 * @see Tail
 * @see Xargs
 * @see Print
 * @see Cat
 */
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
    class Sed(val pattern: String, val replacements: String, val isRegex: Boolean = true) : PatternActionFunc("sed")

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

    /**
     * Select subclass for selecting one or more elements.
     */
    class From(val variables: List<VariableElement>) : PatternActionFunc("select")

    /**
     * Where subclass for filtering elements.
     */
    class Where(val statement: Statement) : PatternActionFunc("where")

    /**
     * OrderBy subclass for ordering elements.
     */
    class Select(val statements: List<Statement>) : PatternActionFunc("orderBy")
}