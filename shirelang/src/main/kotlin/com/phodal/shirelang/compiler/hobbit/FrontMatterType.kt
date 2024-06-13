package com.phodal.shirelang.compiler.hobbit

class ShirePatternAction(val pattern: String, val processors: List<PatternFun>)

/**
 * Basic Types: [STRING], [NUMBER], [DATE], [BOOLEAN], [ARRAY], [OBJECT]
 * Pattern: [PATTERN]
 * Expression: [CaseMatch]
 */
sealed class FrontMatterType(val value: Any) {
    class STRING(value: String) : FrontMatterType(value)
    class NUMBER(value: Int) : FrontMatterType(value)
    class DATE(value: String) : FrontMatterType(value)
    class BOOLEAN(value: Boolean) : FrontMatterType(value)
    class ARRAY(value: List<FrontMatterType>) : FrontMatterType(value)
    class OBJECT(value: Map<String, FrontMatterType>) : FrontMatterType(value)

    /**
     * The default pattern action handles for processing
     */
    class PATTERN(value: ShirePatternAction) : FrontMatterType(value)

    /**
     * The case match for the front matter.
     */
    class CaseMatch(value: Map<String, PATTERN>) : FrontMatterType(value)

    /**
     * Variable same start with $, other will same to String or IDENTIFIER
     */
    class Variable(value: String) : FrontMatterType(value)

    /**
     * IDENTIFIER
     */
    class IDENTIFIER(value: String) : FrontMatterType(value)

    /**
     * The expression
     */
    class Expression(value: Statement) : FrontMatterType(value)
}
