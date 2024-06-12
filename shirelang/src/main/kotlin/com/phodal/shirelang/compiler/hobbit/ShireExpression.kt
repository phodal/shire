package com.phodal.shirelang.compiler.hobbit

/**
 * Represents the base class for all statements.
 */
abstract class Statement

/**
 * Enumeration of operator types used in logical and comparison expressions.
 *
 * @property display The string representation of the operator.
 */
sealed class OperatorType(val display: String) {
    /** Logical OR operator (||). */
    object Or : OperatorType("||")

    /** Logical AND operator (&&). */
    object And : OperatorType("&&")

    /** Logical NOT operator (!). */
    object Not : OperatorType("!")

    /** Equality operator (==). */
    object Equal : OperatorType("==")

    /** Inequality operator (!=). */
    object NotEqual : OperatorType("!=")

    /** Less than operator (<). */
    object LessThan : OperatorType("<")

    /** Greater than operator (>). */
    object GreaterThan : OperatorType(">")

    /** Less than or equal operator (<=). */
    object LessEqual : OperatorType("<=")

    /** Greater than or equal operator (>=). */
    object GreaterEqual : OperatorType(">=")
}

/**
 * Enumeration of string operator types used in string comparison expressions.
 *
 * @property display The string representation of the string operator.
 */
sealed class StringOperator(val display: String) {
    /** Contains operator (contains). */
    object Contains : StringOperator("contains")

    /** Starts with operator (startsWith). */
    object StartsWith : StringOperator("startsWith")

    /** Ends with operator (endsWith). */
    object EndsWith : StringOperator("endsWith")

    /** Matches regex operator (matches). */
    object Matches : StringOperator("matches")
}

/**
 * Represents an operator used in a comparison expression.
 *
 * @property type The type of operator.
 */
data class Operator(val type: OperatorType) : Statement()

/**
 * Represents a string operator used in a string comparison expression.
 *
 * @property type The type of string operator.
 */
data class StringOperatorStatement(val type: StringOperator) : Statement()

/**
 * Represents a comparison expression, including a variable, an operator, and a value.
 *
 * @property variable The name of the variable being compared.
 * @property operator The operator used for comparison.
 * @property value The value being compared against.
 */
data class Comparison(
    val variable: FrontMatterType,
    val operator: Operator,
    val value: FrontMatterType
) : Statement()

/**
 * Represents a string comparison expression, including a variable, a string operator, and a value.
 *
 * @property variable The name of the variable being compared.
 * @property operator The string operator used for comparison.
 * @property value The string value being compared against.
 */
data class StringComparison(
    val variable: String,
    val operator: StringOperatorStatement,
    val value: String
) : Statement()

/**
 * Represents a logical expression, including left and right operands and an operator.
 *
 * @property left The left operand of the logical expression.
 * @property operator The logical operator used in the expression.
 * @property right The right operand of the logical expression.
 */
data class LogicalExpression(
    val left: Statement,
    val operator: OperatorType,
    val right: Statement
) : Statement()

/**
 * Represents a negation expression, including an operand.
 *
 * @property operand The operand to be negated.
 */
data class NotExpression(val operand: Statement) : Statement()


/**
 * Represents a method call expression, including the object and method being called.
 *
 * @property objectName The name of the object on which the method is called.
 * @property methodName The name of the method being called.
 * @property arguments The arguments passed to the method.
 */
data class MethodCall(
    val objectName: FrontMatterType,
    val methodName: FrontMatterType,
    val arguments: List<Any>
) : Statement()
