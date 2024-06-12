package com.phodal.shirelang.compiler.hobbit

class ShirePatternAction(val pattern: String, val processors: List<PatternFun>)

enum class Operator {
    OR, AND, NOT,
    EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL
}

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
}

class Comparison(val variable: String, val operator: Operator, val value: FrontMatterType)

class Factor(val notOp: Boolean = false, val comparison: Comparison)

class Term(val factors: List<Factor>, val andOps: List<Boolean>)
/**
 * The condition expression for the front matter.
 *
 * should include variables like `$selection`
 *
 * should include operators like `&&`, `||`, `!`
 *
 */
class Expr(val terms: List<Term>, val orOps: List<Boolean>)
