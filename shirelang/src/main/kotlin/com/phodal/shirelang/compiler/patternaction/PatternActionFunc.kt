package com.phodal.shirelang.compiler.patternaction

import com.phodal.shirelang.compiler.hobbit.ast.CaseKeyValue
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement

/**
 * The `PatternActionFunc` is a sealed class in Kotlin that represents a variety of pattern action functions.
 * Each subclass represents a different function, and each has a unique set of properties relevant to its function.
 *
 * @property funcName The name of the function.
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
     *
     * For example, `sed("foo", "bar")` would replace all instances of "foo" with "bar".
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
    class From(val variables: List<VariableElement>) : PatternActionFunc("from")

    /**
     * Where subclass for filtering elements.
     */
    class Where(val statement: Statement) : PatternActionFunc("where")

    /**
     * OrderBy subclass for ordering elements.
     */
    class Select(val statements: List<Statement>) : PatternActionFunc("select")

    /**
     * Execute a shire script
     */
    class ExecuteShire(val filename: String, val variableNames: Array<String>) : PatternActionFunc("execute")

    /**
     * use IDE Notify
     */
    class Notify(val message: String) : PatternActionFunc("notify")

    /**
     * Case Match
     */
    class CaseMatch(val keyValue: List<CaseKeyValue>) : PatternActionFunc("switch")

    /**
     * Splitting
     */
    class Splitting(val paths: Array<String>) : PatternActionFunc("splitting")

    /**
     * Embedding text
     */
    class Embedding( val entries: Array<String>) : PatternActionFunc("embedding")

    /**
     * searching text
     */
    class Searching(val text: String, val threshold: Double = 0.5) : PatternActionFunc("searching")

    /**
     * Caching semantic
     */
    class Caching(val text: String) : PatternActionFunc("caching")

    /**
     * Reranking the result
     */
    class Reranking(val type: String) : PatternActionFunc("reranking")

    /**
     * The Redact class is designed for handling sensitive data by applying a specified redaction strategy.
     *
     * @param strategy The redaction strategy to be used. This string defines how the sensitive data will be handled or obscured.
     */
    class Redact(val strategy: String) : PatternActionFunc("redact")

    /**
     * The Crawl function is used to crawl a list of urls, get markdown from html and save it to a file.
     *
     * @param urls The urls to crawl.
     */
    class Crawl(vararg val urls: String) : PatternActionFunc("crawl")

    /**
     * The capture function used to capture file by NodeType
     *
     * @param fileName The file name to save the capture to.
     * @param nodeType The node type to capture.
     */
    class Capture(val fileName: String, val nodeType: String) : PatternActionFunc("capture")

    /**
     * the thread function will run the function in a new thread
     *
     * @param fileName The file name to run
     */
    class Thread(val fileName: String) : PatternActionFunc("thread")

    /**
     * User Custom Functions
     */
    class UserCustom(override val funcName: String, val args: List<String>) : PatternActionFunc(funcName) {
        override fun toString(): String {
            return "$funcName(${args.joinToString(", ")})"
        }
    }
}
