package com.phodal.shirelang.compiler.patternaction

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.NlsSafe
import com.phodal.shirelang.compiler.hobbit.ast.CaseKeyValue
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement
import com.phodal.shirelang.compiler.parser.HobbitHoleParser

/**
 * The `PatternActionFunc` is a sealed class in Kotlin that represents a variety of pattern action functions.
 * Each subclass represents a different function, and each has a unique set of properties relevant to its function.
 *
 * @property funcName The name of the function.
 */
sealed class PatternActionFunc(val type: PatternActionFuncType) {
    open val funcName: String = type.funcName

    /**
     * Grep subclass for searching with one or more patterns.
     *
     * @property patterns The patterns to search for.
     */
    class Grep(vararg val patterns: String) : PatternActionFunc(PatternActionFuncType.GREP)

    /**
     * Find subclass for searching with text
     * @property text The text to search for.
     */
    class Find(val text: String) : PatternActionFunc(PatternActionFuncType.FIND)

    /**
     * Sed subclass for find and replace operations.
     *
     * @property pattern The pattern to search for.
     * @property replacements The string to replace matches with.
     *
     * For example, `sed("foo", "bar")` would replace all instances of "foo" with "bar".
     */
    class Sed(val pattern: String, val replacements: String, val isRegex: Boolean = true) :
        PatternActionFunc(PatternActionFuncType.SED)

    /**
     * Sort subclass for sorting with one or more arguments.
     *
     * @property arguments The arguments to use for sorting.
     */
    class Sort(vararg val arguments: String) : PatternActionFunc(PatternActionFuncType.SORT)

    /**
     * Uniq subclass for removing duplicates based on one or more arguments.
     *
     * @property texts The texts to process for uniqueness.
     */
    class Uniq(vararg val texts: String) : PatternActionFunc(PatternActionFuncType.UNIQ)

    /**
     * Head subclass for retrieving the first few lines.
     *
     * @property number The number of lines to retrieve from the start.
     */
    class Head(val number: Number) : PatternActionFunc(PatternActionFuncType.HEAD)

    /**
     * Tail subclass for retrieving the last few lines.
     *
     * @property number The number of lines to retrieve from the end.
     */
    class Tail(val number: Number) : PatternActionFunc(PatternActionFuncType.TAIL)

    /**
     * Xargs subclass for processing one or more variables.
     *
     * @property variables The variables to process.
     */
    class Xargs(vararg val variables: String) : PatternActionFunc(PatternActionFuncType.XARGS)

    /**
     * Print subclass for printing one or more texts.
     *
     * @property texts The texts to be printed.
     */
    class Print(vararg val texts: String) : PatternActionFunc(PatternActionFuncType.PRINT)

    /**
     * Cat subclass for concatenating one or more files.
     * Paths can be absolute or relative to the current working directory.
     */
    class Cat(vararg val paths: String) : PatternActionFunc(PatternActionFuncType.CAT)

    /**
     * Select subclass for selecting one or more elements.
     */
    class From(val variables: List<VariableElement>) : PatternActionFunc(PatternActionFuncType.FROM)

    /**
     * Where subclass for filtering elements.
     */
    class Where(val statement: Statement) : PatternActionFunc(PatternActionFuncType.WHERE)

    /**
     * OrderBy subclass for ordering elements.
     */
    class Select(val statements: List<Statement>) : PatternActionFunc(PatternActionFuncType.SELECT)

    /**
     * Execute a shire script
     */
    class ExecuteShire(val filename: String, val variableNames: Array<String>) :
        PatternActionFunc(PatternActionFuncType.EXECUTE)

    /**
     * Use IDE Notify
     */
    class Notify(val message: String) : PatternActionFunc(PatternActionFuncType.NOTIFY)

    /**
     * Case Match
     */
    class CaseMatch(val keyValue: List<CaseKeyValue>) : PatternActionFunc(PatternActionFuncType.CASE_MATCH)

    /**
     * Splitting
     */
    class Splitting(val paths: Array<String>) : PatternActionFunc(PatternActionFuncType.SPLITTING)

    /**
     * Embedding text
     */
    class Embedding(val entries: Array<String>) : PatternActionFunc(PatternActionFuncType.EMBEDDING)

    /**
     * Searching text
     */
    class Searching(val text: String, val threshold: Double = 0.5) : PatternActionFunc(PatternActionFuncType.SEARCHING)

    /**
     * Caching semantic
     */
    class Caching(val text: String) : PatternActionFunc(PatternActionFuncType.CACHING)

    /**
     * Reranking the result
     */
    class Reranking(val strategy: String) : PatternActionFunc(PatternActionFuncType.RERANKING)

    /**
     * The Redact class is designed for handling sensitive data by applying a specified redaction strategy.
     *
     * @param strategy The redaction strategy to be used. This string defines how the sensitive data will be handled or obscured.
     */
    class Redact(val strategy: String) : PatternActionFunc(PatternActionFuncType.REDACT)

    /**
     * The Crawl function is used to crawl a list of urls, get markdown from html and save it to a file.
     *
     * @param urls The urls to crawl.
     */
    class Crawl(vararg val urls: String) : PatternActionFunc(PatternActionFuncType.CRAWL)

    /**
     * The capture function used to capture file by NodeType
     *
     * @param fileName The file name to save the capture to.
     * @param nodeType The node type to capture.
     */
    class Capture(val fileName: String, val nodeType: String) : PatternActionFunc(PatternActionFuncType.CAPTURE)

    /**
     * The thread function will run the function in a new thread
     *
     * @param fileName The file name to run
     */
    class Thread(val fileName: String, val variableNames: Array<String>) :
        PatternActionFunc(PatternActionFuncType.THREAD)

    /**
     * The jsonpath function will parse the json and get the value by jsonpath
     */
    class JsonPath(val obj: String?, val path: String, val sseMode: Boolean = false) :
        PatternActionFunc(PatternActionFuncType.JSONPATH)

    class Destroy : PatternActionFunc(PatternActionFuncType.DESTROY)

    class Batch(val fileName: String, val inputs: List<String>, val batchSize: Int = 1) : PatternActionFunc(PatternActionFuncType.BATCH)

    /**
     * User Custom Functions
     */
    class ToolchainFunction(override val funcName: String, val args: List<String>) :
        PatternActionFunc(PatternActionFuncType.TOOLCHAIN_FUNCTION) {
        override fun toString(): String {
            return "$funcName(${args.joinToString(", ")})"
        }
    }

    companion object {
        private val logger = logger<PatternActionFunc>()

        fun findDocByName(funcName: String?): String? {
            return PatternActionFuncType.values().find { it.funcName == funcName }?.description
        }

        fun from(funcName: String, args: List<String>): PatternActionFunc? {
            return when (PatternActionFuncType.values().find { it.funcName == funcName }) {
                PatternActionFuncType.GREP -> {
                    if (args.isEmpty()) {
                        logger.error("parsePatternAction, grep requires at least 1 argument")
                        return null
                    }
                    Grep(*args.toTypedArray())
                }

                PatternActionFuncType.SORT -> Sort(*args.toTypedArray())

                PatternActionFuncType.FIND -> {
                    if (args.isEmpty()) {
                        logger.error("parsePatternAction, find requires at least 1 argument")
                        return null
                    }
                    Find(args[0])
                }

                PatternActionFuncType.SED -> {
                    if (args.size < 2) {
                        logger.error("parsePatternAction, sed requires at least 2 arguments")
                        return null
                    }
                    if (args[0].startsWith("/") && args[0].endsWith("/")) {
                        Sed(args[0], args[1], true)
                    } else {
                        Sed(args[0], args[1])
                    }
                }

                PatternActionFuncType.XARGS -> Xargs(*args.toTypedArray())

                PatternActionFuncType.UNIQ -> Uniq(*args.toTypedArray())

                PatternActionFuncType.HEAD -> {
                    if (args.isEmpty()) {
                        Head(10)
                    } else {
                        Head(args[0].toInt())
                    }
                }

                PatternActionFuncType.TAIL -> {
                    if (args.isEmpty()) {
                        Tail(10)
                    } else {
                        Tail(args[0].toInt())
                    }
                }

                PatternActionFuncType.PRINT -> Print(*args.toTypedArray())

                PatternActionFuncType.CAT -> Cat(*args.toTypedArray())

                PatternActionFuncType.EXECUTE -> {
                    val first = args.firstOrNull() ?: ""
                    val rest = args.drop(1).toTypedArray()
                    ExecuteShire(first, rest)
                }

                PatternActionFuncType.NOTIFY -> Notify(args[0])

                PatternActionFuncType.EMBEDDING -> Embedding(args.toTypedArray())

                PatternActionFuncType.SPLITTING -> Splitting(args.toTypedArray())

                PatternActionFuncType.SEARCHING -> Searching(
                    args[0],
                    args.getOrNull(1)?.toDouble() ?: 0.5
                )

                PatternActionFuncType.RERANKING -> {
                    val first = args.firstOrNull() ?: "default"
                    Reranking(first)
                }

                PatternActionFuncType.CACHING -> Caching(args[0])

                PatternActionFuncType.REDACT -> {
                    val first = args.firstOrNull() ?: "default"
                    Redact(first)
                }

                PatternActionFuncType.CRAWL -> {
                    val urls: List<String> = args.filter { it.trim().isNotEmpty() }
                    Crawl(*urls.toTypedArray())
                }

                PatternActionFuncType.CAPTURE -> {
                    if (args.size < 2) {
                        logger.error("parsePatternAction, capture requires at least 2 arguments")
                        return null
                    }
                    Capture(args[0], args[1])
                }

                PatternActionFuncType.THREAD -> {
                    if (args.isEmpty()) {
                        logger.error("parsePatternAction, thread requires at least 1 argument")
                        return null
                    }
                    val rest = args.drop(1).toTypedArray()
                    Thread(args.first(), rest)
                }

                PatternActionFuncType.JSONPATH -> {
                    if (args.isEmpty()) {
                        logger.error("parsePatternAction, jsonpath requires at least 1 argument")
                        return null
                    }
                    if (args.size < 2) {
                        JsonPath(null, args[0], false)
                    } else {
                        when (args[1]) {
                            "true" -> JsonPath(null, args[0], true)
                            else -> JsonPath(args[0], args[1])
                        }
                    }
                }

                PatternActionFuncType.FROM,
                PatternActionFuncType.WHERE,
                PatternActionFuncType.SELECT,
                PatternActionFuncType.CASE_MATCH,
                    -> {
                    ToolchainFunction(funcName, args)
                }

                PatternActionFuncType.BATCH -> {
                    Batch(args[0], args.drop(1))
                }
                PatternActionFuncType.DESTROY -> {
                    Destroy()
                }
                PatternActionFuncType.TOOLCHAIN_FUNCTION -> ToolchainFunction(funcName, args)
                else -> {
                    ToolchainFunction(funcName, args)
                }
            }
        }
    }
}
