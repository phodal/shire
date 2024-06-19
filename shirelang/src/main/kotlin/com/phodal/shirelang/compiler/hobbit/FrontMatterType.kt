package com.phodal.shirelang.compiler.hobbit

import com.phodal.shirelang.compiler.hobbit.patternaction.PatternActionFunc


class ShirePatternAction(val pattern: String, val processors: List<PatternActionFunc>)

/**
 * Basic Types: [STRING], [NUMBER], [DATE], [BOOLEAN], [ARRAY], [OBJECT]
 * Pattern: [PATTERN]
 * Expression: [CaseMatch]
 */
sealed class FrontMatterType(val value: Any) {
    open fun display(): String = value.toString()

    class STRING(value: String) : FrontMatterType(value) {
        override fun display(): String {
            return "\"$value\""
        }
    }

    class NUMBER(value: Int) : FrontMatterType(value) {
        override fun display(): String {
            return value.toString()
        }
    }

    /**
     * The `DATE` class is a subclass of `FrontMatterType` that represents a date.
     */
    class DATE(value: String) : FrontMatterType(value) {
        override fun display(): String {
            return value.toString()
        }
    }

    class BOOLEAN(value: Boolean) : FrontMatterType(value) {
        override fun display(): String {
            return value.toString()
        }
    }

    /**
     * The `ARRAY` class is a subclass of `FrontMatterType` that represents a JSON array.
     *
     * ```shire
     * ---
     * variables: ["var1", "var2"]
     * ---
     */
    class ARRAY(value: List<FrontMatterType>) : FrontMatterType(value) {
        override fun display(): String {
            return (value as List<FrontMatterType>).joinToString(", ", "[", "]") { it.display() }
        }
    }

    /**
     * The `OBJECT` class is a subclass of `FrontMatterType` that represents a JSON object.
     * It takes a `Map` of `String` to `FrontMatterType` as its constructor parameter.
     *
     * ```shire
     * ---
     * variables:
     *   "var1": "value1"
     * ---
     * ```
     */
    class OBJECT(value: Map<String, FrontMatterType>) : FrontMatterType(value) {
        override fun display(): String {
            return (value as Map<String, FrontMatterType>).entries.joinToString(
                ", ",
                "{",
                "}"
            ) { "\"${it.key}\": ${it.value.display()}" }
        }
    }

    /**
     * The pattern action handles for processing
     *
     * ```shire
     * ---
     * variables:
     *   "var2": /.*.java/ { grep("error.log") | sort | xargs("rm")}
     * ---
     * ````
     */
    class PATTERN(value: ShirePatternAction) : FrontMatterType(value) {
        override fun display(): String {
            return (value as ShirePatternAction).pattern + " -> " + (value.processors.joinToString(", ") { it.funcName })
        }
    }

    /**
     * The case match for the front matter.
     *
     * ```shire
     * ---
     * case "$0" {
     *      "error" { grep("ERROR") | sort | xargs("notify_admin") }
     *      "warn" { grep("WARN") | sort | xargs("notify_admin") }
     *      "info" { grep("INFO") | sort | xargs("notify_user") }
     *      default  { grep("ERROR") | sort | xargs("notify_admin") }
     * }
     * ---
     */
    class CaseMatch(value: Map<String, PATTERN>) : FrontMatterType(value) {
        /**
         * output sample:
         * ```shire
         * case "$0" {
         *       "error" { grep("ERROR") | sort | xargs("notify_admin") }
         *       "warn" { grep("WARN") | sort | xargs("notify_admin") }
         *       "info" { grep("INFO") | sort | xargs("notify_user") }
         *       default  { grep("ERROR") | sort | xargs("notify_admin") }
         * }
         * ```s
         */
        override fun display(): String {
            return (value as Map<String, PATTERN>).entries.joinToString(
                "\n",
                "case \"\$0\" {\n",
                "\n}"
            ) { (key, value: PATTERN) ->
                val pattern = (value as ShirePatternAction).pattern
                val processors = value.processors.joinToString(" | ") { it.funcName }
                "    \"$key\" { $processors }"
            }
        }
    }

    /**
     * Variable same start with $, other will same to String or IDENTIFIER
     */
    class Variable(value: String) : FrontMatterType(value) {
        override fun display(): String {
            return "\$$value"
        }
    }

    /**
     * The simple expression for the [HobbitHole.WHEN] condition.
     *
     * ```shire
     * ---
     * when: $selection.length >= 1 && $selection.first() == 'p'
     * ---
     * ```
     */
    class Expression(value: Statement) : FrontMatterType(value) {
        override fun display(): String {
            return (value as Statement).display()
        }
    }



    /**
     * Identifier for the front matter config expression and template, like [Expression] or [MethodCall]
     *
     * ```shire
     * ---
     * when: $selection.length >= 1 && $selection.first() == 'p'
     * ---
     * ```
     */
    class IDENTIFIER(value: String) : FrontMatterType(value) {
        override fun display(): String {
            return value.toString()
        }
    }

}
