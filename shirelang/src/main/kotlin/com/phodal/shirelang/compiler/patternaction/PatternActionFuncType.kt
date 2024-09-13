package com.phodal.shirelang.compiler.patternaction

import org.intellij.lang.annotations.Language

/**
 * `PatternActionFuncType` was for show documentation when user hovers on the function.
 */
enum class PatternActionFuncType(val funcName: String, val description: String) {
    GREP(
        "grep", """
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
        """.trimMargin()
    ),
    FIND(
        "find", """
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
        """.trimMargin()
    ),
    SED(
        "sed", """
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
    """.trimMargin()
    ),
    PRINT(
        "print", """
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
    """.trimMargin()
    ),
    CAT(
        "cat", """
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
    """.trimMargin()
    ),
    EXECUTE(
        "execute", """
        | `execute` function is used to execute a shire script.
        | 
        | Example:
        | 
        | ```shire
        | ---
        | name: "Search"
        | afterStreaming: { execute("search.shire") }
        | ---
        | ```
        | 
    """.trimMargin()
    ),
    NOTIFY(
        "notify",
        """
        | `notify` function is used to send a notification.
        | 
        | Example:
        | 
        | ```shire
        | ---
        | name: "Search"
        | afterStreaming: { notify("Failed to Generate JSON") }
        | ---
        | ```
        | 
    """.trimMargin()
    ),
    CASE_MATCH("switch", "Case Match."),
    SPLITTING(
        "splitting", """
        | `splitting` function is used to split the file into chunks.
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |   "testTemplate": /.*.kt/ { caching("disk") | splitting | embedding }
        | ---
        | ```
        | 
        | Support format: code, txt, pdf, html, doc, xls, ppt, md.
        """.trimMargin()
    ),
    EMBEDDING(
        "embedding", """
        | `embedding` function is used to embedding text.
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |  "testTemplate": /.*.kt/ { caching("disk") | splitting | embedding }
        | ---
        | ```
    """.trimMargin()
    ),
    SEARCHING(
        "searching", """
        | `searching` function is used to search embedding text.
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |   "story": /any/ { caching("disk") | splitting | embedding | searching("epic") }
        | ---
        | ```
        | with threshold:
        |
        | ```shire
        | ---
        | variables:
        |  "story": /any/ { caching("disk") | splitting | embedding | searching("epic", 0.5) }
        | ---
        | ```
        | 
        """.trimMargin()
    ),
    CACHING(
        "caching", """
        | `caching` function is used to cache the semantic. support "disk" and "memory", default is "memory".
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |  "testTemplate": /.*.kt/ { caching("disk") }
        |  "story": /any/ { caching("memory") }
        | ---
        | ```
        |  
    """.trimMargin()
    ),
    RERANKING(
        "reranking", """
        | `reranking` function is used to rerank the result. current only support "Lost In Middle" pattern
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |    "story": /any/ {  caching("disk") | splitting | embedding | reranking }
        | ---
        | ```
        | 
    """.trimMargin()
    ),
    REDACT(
        "redact", """
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
    """.trimMargin()
    ),
    CRAWL(
        "crawl", """
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
    """.trimMargin()
    ),
    CAPTURE(
        "capture", """
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
    """.trimMargin()
    ),
    THREAD(
        "thread",
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
    JSONPATH(
        "jsonpath", """
        | The `jsonpath` function will parse the json and get the value by jsonpath.
        | 
        | Example:
        | ```shire
        | ---
        | variables:
        |   "api": /sampl.sh/ { thread(".shire/toolchain/bigmodel.curl.sh") | jsonpath("${'$'}.choices[0].message.content") }
        | ---
        | ```
        | 
        | With SSE Example:
        | ```shire
        | ---
        | variables:
        |   "story": /any/ { thread(".shire/shell/dify-epic-story.curl.sh") | jsonpath("${'$'}.answer", true) }
        | ---
        | ```
        | 
        | """.trimMargin()
    ),
    SORT("sort", "Sort subclass for sorting with one or more arguments."),
    UNIQ("uniq", "Uniq subclass for removing duplicates based on one or more arguments."),
    HEAD(
        "head", """
        |Head subclass for retrieving the first few lines.
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "controllers": /.*.java/ { find("Controller") | grep("src/main/java/.*") | head(1)  | cat }
        |---
        |```
        """.trimMargin()
    ),
    TAIL("tail", "Tail subclass for retrieving the last few lines."),
    XARGS("xargs", "Xargs subclass for processing one or more variables."),
    FROM("from", "Select subclass for selecting one or more elements."),
    WHERE("where", "Where subclass for filtering elements."),
    SELECT("select", "OrderBy subclass for ordering elements."),
    TOOLCHAIN_FUNCTION(
        "toolchain", """
        |`toolchain` function is define by the different IDE plugins, for example, the Database plugin, the Shell plugin, etc.
        |
        |Example:
        |
        |```shire
        |---
        |variables:
        |  "relatedTableInfo": /./ { column("user", "post", "tag") }
        |---
        |```
        """.trimMargin()
    );

    override fun toString(): String = description
}