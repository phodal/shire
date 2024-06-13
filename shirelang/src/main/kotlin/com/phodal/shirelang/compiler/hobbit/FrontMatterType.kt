package com.phodal.shirelang.compiler.hobbit

class ShirePatternAction(val pattern: String, val processors: List<PatternFun>)

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

    class ARRAY(value: List<FrontMatterType>) : FrontMatterType(value) {
        override fun display(): String {
            return (value as List<FrontMatterType>).joinToString(", ", "[", "]") { it.display() }
        }
    }

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
     * The default pattern action handles for processing
     */
    class PATTERN(value: ShirePatternAction) : FrontMatterType(value) {
        override fun display(): String {
            return (value as ShirePatternAction).pattern + " -> " + (value.processors.joinToString(", ") { it.funcName })
        }
    }

    /**
     * The case match for the front matter.
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
     * IDENTIFIER
     */
    class IDENTIFIER(value: String) : FrontMatterType(value) {
        override fun display(): String {
            return value.toString()
        }
    }

    /**
     * The expression
     */
    class Expression(value: Statement) : FrontMatterType(value) {
        override fun display(): String {
            return (value as Statement).display()
        }
    }
}
