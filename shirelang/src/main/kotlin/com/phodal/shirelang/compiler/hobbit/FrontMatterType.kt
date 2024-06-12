package com.phodal.shirelang.compiler.hobbit

import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.psi.ShireTypes

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
}

abstract class Statement

/**
 * Enumeration of operator types used in conditional expressions.
 */
sealed class OperatorType(val type: IElementType, val display: String) {
    object Or : OperatorType(ShireTypes.OROR, "||")
    object And : OperatorType(ShireTypes.ANDAND, "&&")
    object Not : OperatorType(ShireTypes.NOT, "!")
    object Equal : OperatorType(ShireTypes.EQEQ, "==")
    object NotEqual : OperatorType(ShireTypes.NEQ, "!=")
    object LessThan : OperatorType(ShireTypes.LT, "<")
    object GreaterThan : OperatorType(ShireTypes.GT, ">")
    object LessEqual : OperatorType(ShireTypes.LTE, "<=")
    object GreaterEqual : OperatorType(ShireTypes.GTE, ">=")
}

/**
 * String Operator
 */
sealed class StringOperator(val display: String) {
    object Contains : StringOperator("contains")
    object StartsWith : StringOperator("startsWith")
    object EndsWith : StringOperator("endsWith")
    object Matches : StringOperator("matches")
}

/**
 * Represents an operator used in a comparison expression.
 *
 * @property type The type of operator.
 */
class Operator(val type: IElementType) : Statement() {
    val operatorType: OperatorType = toType()

    /**
     * This function converts a ShireTypes enum value to its corresponding OperatorType enum value.
     * It uses a when expression to match the input ShireTypes enum value to the appropriate OperatorType enum value.
     * If the input ShireTypes enum value does not match any of the predefined cases, it throws an IllegalArgumentException.
     *
     * @return the OperatorType enum value corresponding to the input ShireTypes enum value
     * @throws IllegalArgumentException if the input ShireTypes enum value is not recognized
     */
    private fun toType(): OperatorType {
        return when (type) {
            ShireTypes.OROR -> OperatorType.Or
            ShireTypes.ANDAND -> OperatorType.And
            ShireTypes.NOT -> OperatorType.Not
            ShireTypes.EQEQ -> OperatorType.Equal
            ShireTypes.NEQ -> OperatorType.NotEqual
            ShireTypes.LT -> OperatorType.LessThan
            ShireTypes.GT -> OperatorType.GreaterThan
            ShireTypes.LTE -> OperatorType.LessEqual
            ShireTypes.GTE -> OperatorType.GreaterEqual
            else -> throw IllegalArgumentException("Invalid operator type")
        }
    }

    fun toSymbol(): String {
        return when (type) {
            ShireTypes.OROR -> "||"
            ShireTypes.ANDAND -> "&&"
            ShireTypes.NOT -> "!"
            ShireTypes.EQEQ -> "=="
            ShireTypes.NEQ -> "!="
            ShireTypes.LT -> "<"
            ShireTypes.GT -> ">"
            ShireTypes.LTE -> "<="
            ShireTypes.GTE -> ">="
            else -> throw IllegalArgumentException("Invalid operator type")
        }

    }
}

/**
 * Represents a comparison expression, including a variable, an operator, and a value.
 *
 * @property variable The name of the variable being compared.
 * @property operator The operator used for comparison.
 * @property value The value being compared against.
 */
class Comparison(val variable: String, val operator: Operator, val value: FrontMatterType) : Statement()

/**
 * Represents a factor in a conditional expression, including an optional NOT operator and a comparison expression.
 *
 * @property notOp Indicates if the NOT operator is included, default is false.
 * @property comparison The comparison expression contained within the factor.
 */
class Factor(val notOp: Boolean = false, val comparison: Comparison) : Statement()

/**
 * Represents a term in a conditional expression, consisting of multiple factors combined with AND operators.
 *
 * @property factors The list of factors in the term.
 */
class Term(val factors: List<Factor>) : Statement()

/**
 * Represents a complete conditional expression, consisting of multiple terms combined with OR operators.
 *
 * @property terms The list of terms in the expression.
 */
class Expression(val terms: List<Term>) : Statement()
