package com.phodal.shirelang.compiler.patternaction

import com.phodal.shirelang.compiler.hobbit.ast.CaseKeyValue
import com.phodal.shirelang.compiler.hobbit.ast.Statement
import com.phodal.shirelang.compiler.hobbit.ast.VariableElement

enum class PatternActionFuncType(val funcName: String, val description: String) {
    GREP("grep", """
        |Grep subclass for searching with one or more regex patterns.
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "controllers": /.*.java/ { cat | grep("class\s+([a-zA-Z]*Controller)")  }
        |---
        |```
        """.trimMargin()),
    FIND("find", """
        |Find subclass for searching with text.
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /any/ { find("epic") }
        |---
        |```
        """.trimMargin()),
    SED("sed", """
        |Sed subclass for find and replace operations. 
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "var2": /.*ple.shire/ { cat | find("openai") | sed("(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|${'$'})", "sk-***") }
        |---
        |```
    """.trimMargin()),
    SORT("sort", "Sort subclass for sorting with one or more arguments."),
    UNIQ("uniq", "Uniq subclass for removing duplicates based on one or more arguments."),
    HEAD("head", "Head subclass for retrieving the first few lines."),
    TAIL("tail", "Tail subclass for retrieving the last few lines."),
    XARGS("xargs", "Xargs subclass for processing one or more variables."),
    PRINT("print", """
        |`print` function is used to print text or last output. 
        |
        |Last output Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /BlogController\.java/ { print }
        |---
        |```
        |
        |Text content Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /any/ { print("hello world") }
        |---  
        |```
    """.trimMargin()),
    CAT("cat", """
        |`cat` function is used to concatenate one or more files.
        |
        |Paths can be absolute or relative to the current working directory.
        |
        |Last output Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /BlogController\.java/ { cat }
        |---
        | 
        |File path Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /any/ { cat("file.txt") }
        |---  
        |```
    """.trimMargin()),
    FROM("from", "Select subclass for selecting one or more elements."),
    WHERE("where", "Where subclass for filtering elements."),
    SELECT("select", "OrderBy subclass for ordering elements."),
    EXECUTE("execute", """
        | `execute` function is used to execute a shire script.
        | 
        | Example:
        | ---
        | name: "Search"
        | variables:
        |   "testTemplate": /.*.kt/ { caching("disk") | splitting | embedding }
        | afterStreaming: { searching(${'$'}output) | execute("search.shire") }
        | ---
        | 
    """.trimMargin()),
    NOTIFY("notify", "Use IDE Notify."),
    CASE_MATCH("switch", "Case Match."),
    SPLITTING("splitting", "Splitting."),
    EMBEDDING("embedding", "Embedding text."),
    SEARCHING("searching", "Searching text."),
    CACHING("caching", "Caching semantic."),
    RERANKING("reranking", "Reranking the result."),
    REDACT("redact", """
        | `redact` class is designed for handling sensitive data by applying a specified redaction strategy.
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |   "phoneNumber": "086-1234567890"
        |   "var2": /.*ple.shire/ { cat | redact }
        | ---
        | ```    
    """.trimMargin()),
    CRAWL("crawl", """
        | `crawl` function is used to crawl a list of urls, get markdown from html and save it to a file.
        | 
        | Example: 
        | ```shire
        | ---
        | variables:
        |   "websites": /*\.md/ { capture("docs/crawlSample.md", "link") | crawl() | thread("summary.shire") }
        |   "confluence": { thread("confluence.bash", param1, param2) }
        |   "pythonNode.js": { thread("python.py", param1, param2) }  
        | ---
        | ```
        | 
    """.trimMargin()),
    CAPTURE("capture", """
        | `capture` function used to capture url link by NodeType, support Markdown only for now.
        | 
        | Example: 
        | ```shire
        | ---
        | variables:
        |   "websites": /*\.md/ { capture("docs/crawlSample.md", "link") | crawl() | thread("summary.shire") }
        |   "confluence": { thread("confluence.bash", param1, param2) }
        |   "pythonNode.js": { thread("python.py", param1, param2) }  
        | ---
        | ```
        | 
    """.trimMargin()),
    THREAD("thread",
        """
        |`thread` function will run the function in a new thread
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "story": /any/ { thread(".shire/shell/dify-epic-story.curl.sh") | jsonpath("${'$'}.answer", true) }
        |---
        |```
        """.trimMargin()
    ),
    JSONPATH("jsonpath", "The jsonpath function will parse the json and get the value by jsonpath."),
    TOOLCHAIN_FUNCTION("toolchain", "User Custom Functions.");
    override fun toString(): String = description
}

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
    class Sed(val pattern: String, val replacements: String, val isRegex: Boolean = true) : PatternActionFunc(PatternActionFuncType.SED)

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
    class ExecuteShire(val filename: String, val variableNames: Array<String>) : PatternActionFunc(PatternActionFuncType.EXECUTE)

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
    class Thread(val fileName: String, val variableNames: Array<String>) : PatternActionFunc(PatternActionFuncType.THREAD)

    /**
     * The jsonpath function will parse the json and get the value by jsonpath
     */
    class JsonPath(val obj: String?, val path: String, val sseMode: Boolean = false) : PatternActionFunc(PatternActionFuncType.JSONPATH)

    /**
     * User Custom Functions
     */
    class ToolchainFunction(override val funcName: String, val args: List<String>) : PatternActionFunc(PatternActionFuncType.TOOLCHAIN_FUNCTION) {
        override fun toString(): String {
            return "$funcName(${args.joinToString(", ")})"
        }
    }

    companion object {
        fun findDocByName(funcName: String?): String? {
            return PatternActionFuncType.values().find { it.funcName == funcName }?.description
        }
    }
}
