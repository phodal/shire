package com.phodal.shirelang.compiler

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compiler.hobbit.*
import com.phodal.shirelang.compiler.hobbit.ast.RuleBasedPatternAction
import com.phodal.shirelang.compiler.hobbit.ast.*
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.psi.*

object FrontmatterParser {
    private val logger = logger<FrontmatterParser>()

    fun hasFrontMatter(file: ShireFile): Boolean {
        return PsiTreeUtil.getChildrenOfTypeAsList(file, ShireFrontMatterHeader::class.java).isNotEmpty()
    }

    /**
     * Parses the given ShireFrontMatterHeader and returns a FrontMatterShireConfig object.
     *
     * @param psiElement the ShireFrontMatterHeader to be parsed
     * @return a FrontMatterShireConfig object if parsing is successful, null otherwise
     */
    fun parse(psiElement: ShireFrontMatterHeader): HobbitHole? {
        return psiElement.children.firstOrNull()?.let {
            val fm = processFrontmatter(it.children)
            HobbitHole.from(fm)
        }
    }

    fun parse(file: ShireFile): HobbitHole? {
        return PsiTreeUtil.getChildrenOfTypeAsList(file, ShireFrontMatterHeader::class.java).firstOrNull()?.let {
            parse(it)
        }
    }

    private fun processFrontmatter(frontMatterEntries: Array<PsiElement>): MutableMap<String, FrontMatterType> {
        val frontMatter: MutableMap<String, FrontMatterType> = mutableMapOf()
        var lastKey = ""

        frontMatterEntries.forEach { entry ->
            entry.children.forEach { child ->
                when (child.elementType) {
                    ShireTypes.FRONT_MATTER_KEY -> {
                        lastKey = child.text
                    }

                    ShireTypes.FRONT_MATTER_VALUE -> {
                        frontMatter[lastKey] = parseFrontMatterValue(child)
                            ?: FrontMatterType.STRING("Frontmatter value parsing failed: ${child.text}")
                    }

                    ShireTypes.PATTERN_ACTION -> {
                        frontMatter[lastKey] = parsePatternAction(child)
                            ?: FrontMatterType.STRING("Pattern action parsing failed: ${child.text}")
                    }

                    ShireTypes.LOGICAL_AND_EXPR -> {
                        lastKey = HobbitHole.WHEN
                        frontMatter[lastKey] = parseLogicAndExprToType(child as ShireLogicalAndExpr)
                            ?: FrontMatterType.STRING("Logical expression parsing failed: ${child.text}")
                    }

                    ShireTypes.LOGICAL_OR_EXPR -> {
                        lastKey = HobbitHole.WHEN
                        frontMatter[lastKey] = parseLogicOrExprToType(child as ShireLogicalOrExpr)
                            ?: FrontMatterType.STRING("Logical expression parsing failed: ${child.text}")
                    }

                    ShireTypes.CALL_EXPR -> {
                        lastKey = HobbitHole.WHEN
                        frontMatter[lastKey] = FrontMatterType.EXPRESSION(parseExpr(child))
                    }

                    ShireTypes.FUNCTION_STATEMENT -> {
                        frontMatter[lastKey] = parseFunction(child as ShireFunctionStatement)
                    }

                    else -> {
                        logger.warn("processFrontmatter, Unknown frontmatter type: ${child.elementType}")
                    }
                }
            }
        }

        return frontMatter
    }

    private fun parseFunction(statement: ShireFunctionStatement): FrontMatterType {
        return when(val body = statement.functionBody?.firstChild) {
            is ShireQueryStatement -> {
                 ShireAstQLParser.parse(body)
            }

            null -> {
                FrontMatterType.EMPTY()
            }
            else -> {
                logger.error("parseFunction, Unknown function type: ${body.elementType}")
                FrontMatterType.STRING("Unknown function type: ${body.elementType}")
            }
        }
    }

    private fun parseLogicAndExprToType(child: ShireLogicalAndExpr): FrontMatterType? {
        val logicalExpression = parseLogicAndExpr(child) ?: return null
        return FrontMatterType.EXPRESSION(logicalExpression)
    }

    private fun parseLogicAndExpr(child: ShireLogicalAndExpr): LogicalExpression? {
        val left = child.exprList.firstOrNull() ?: return null
        val right = child.exprList.lastOrNull() ?: return null

        val logicalExpression = LogicalExpression(
            left = parseExpr(left),
            operator = OperatorType.And,
            right = parseExpr(right)
        )
        return logicalExpression
    }

    private fun parseLogicOrExprToType(child: ShireLogicalOrExpr): FrontMatterType? {
        val logicOrExpr = parseLogicOrExpr(child) ?: return null
        return FrontMatterType.EXPRESSION(logicOrExpr)
    }

    private fun parseLogicOrExpr(child: ShireLogicalOrExpr): LogicalExpression? {
        val left = child.exprList.firstOrNull() ?: return null
        val right = child.exprList.lastOrNull() ?: return null

        val logicOrExpr = LogicalExpression(
            left = parseExpr(left),
            operator = OperatorType.Or,
            right = parseExpr(right)
        )
        return logicOrExpr
    }

    /**
     * This function is used to parse an expression of type PsiElement into a Statement. The type of Statement returned depends on the type of the expression.
     *
     * @param expr The PsiElement expression to be parsed. This expression can be of type CALL_EXPR, EQ_COMPARISON_EXPR, INEQ_COMPARISON_EXPR, or any other type.
     *
     * If the expression is of type CALL_EXPR, the function finds the first child of type ShireExpr and builds a method call with the found ShireExpr and the list of expressions in the ShireCallExpr.
     *
     * If the expression is of type EQ_COMPARISON_EXPR, the function parses the first and last child of the expression into a Comparison statement with an equal operator.
     *
     * If the expression is of type INEQ_COMPARISON_EXPR, the function parses the first and last child of the expression into a Comparison statement with an operator determined by the ineqComparisonOp text of the ShireIneqComparisonExpr.
     *
     * If the expression is of any other type, the function logs a warning and returns a Comparison statement with an equal operator and empty string operands.
     *
     * @return A Statement parsed from the given expression. The type of Statement depends on the type of the expression.
     */
    fun parseExpr(expr: PsiElement): Statement = when (expr.elementType) {
        ShireTypes.CALL_EXPR -> {
            val shireCallExpr = expr as ShireCallExpr
            val expressionList = shireCallExpr.expressionList
            val hasParentheses = expressionList?.prevSibling?.text == "("

            buildMethodCall(shireCallExpr.refExpr, expressionList?.children, hasParentheses)
        }

        ShireTypes.EQ_COMPARISON_EXPR -> {
            val variable = parseRefExpr(expr.children.firstOrNull())
            val value = parseRefExpr(expr.children.lastOrNull())
            Comparison(variable, Operator(OperatorType.Equal), value)
        }

        // INEQ_COMPARISON_EXPR
        ShireTypes.INEQ_COMPARISON_EXPR -> {
            val variable = parseRefExpr(expr.children.firstOrNull())
            val value = parseRefExpr(expr.children.lastOrNull())

            val exp = expr as ShireIneqComparisonExpr
            val operatorType = OperatorType.fromString(exp.ineqComparisonOp.text)

            Comparison(variable, Operator(operatorType), value)
        }

        ShireTypes.LOGICAL_AND_EXPR -> {
            parseLogicAndExpr(expr as ShireLogicalAndExpr)
                ?: Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }

        ShireTypes.LOGICAL_OR_EXPR -> {
            parseLogicOrExpr(expr as ShireLogicalOrExpr)
                ?: Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }

        ShireTypes.REF_EXPR -> {
            val refExpr = expr as ShireRefExpr
            if (refExpr.expr == null) {
                Value(FrontMatterType.IDENTIFIER(refExpr.identifier.text))
            } else {
                val methodCall = buildMethodCall(refExpr, null, false)
                methodCall
            }
        }

        ShireTypes.LITERAL_EXPR -> {
            Value(parseLiteral(expr))
        }

        else -> {
            logger.warn("parseExpr, Unknown expression type: ${expr.elementType}")
            Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }
    }

    private fun parseRefExpr(expr: PsiElement?): FrontMatterType {
        return when (expr?.elementType) {
            ShireTypes.LITERAL_EXPR -> {
                parseLiteral(expr!!)
            }

            // fake refExpr ::= expr? '.' IDENTIFIER
            ShireTypes.REF_EXPR -> {
                val refExpr = expr as ShireRefExpr
                if (refExpr.expr == null) {
                    FrontMatterType.IDENTIFIER(refExpr.identifier.text)
                } else {
                    val methodCall = buildMethodCall(refExpr, null, false)
                    FrontMatterType.EXPRESSION(methodCall)
                }
            }

            ShireTypes.CALL_EXPR -> {
                val shireCallExpr = expr as ShireCallExpr

                val expressionList = shireCallExpr.expressionList
                val hasParentheses = expressionList?.prevSibling?.text == "("

                val methodCall = this.buildMethodCall(expr.refExpr, expressionList?.children, hasParentheses)
                FrontMatterType.EXPRESSION(methodCall)
            }

            else -> {
                logger.warn("parseRefExpr, Unknown expression type: ${expr?.elementType}")
                FrontMatterType.STRING("")
            }
        }
    }


    private fun buildMethodCall(refExpr: ShireRefExpr, expressionList: Array<PsiElement>?, hasParentheses: Boolean): MethodCall {
        val left = if (refExpr.expr == null) {
            FrontMatterType.IDENTIFIER(refExpr.identifier.text)
        } else {
            parseRefExpr(refExpr.expr)
        }

        val id = refExpr.expr?.nextSibling?.nextSibling
        val right = FrontMatterType.IDENTIFIER(id?.text ?: "")

        var args = expressionList?.map {
            parseRefExpr(it)
        }

        // fix for () lost in display()
        if (hasParentheses && args == null) {
            args = emptyList()
        }

        return MethodCall(left, right, args)
    }

    private fun parseLiteral(ref: PsiElement): FrontMatterType {
        val firstChild = ref.firstChild
        return when (firstChild.elementType) {
            ShireTypes.IDENTIFIER -> {
                FrontMatterType.IDENTIFIER(ref.text)
            }

            ShireTypes.NUMBER -> {
                FrontMatterType.NUMBER(ref.text.toInt())
            }

            ShireTypes.QUOTE_STRING -> {
                val value = ref.text.substring(1, ref.text.length - 1)
                FrontMatterType.STRING(value)
            }

            ShireTypes.VARIABLE_START -> {
                val next = ref.lastChild
                FrontMatterType.VARIABLE(next.text)
            }

            else -> {
                logger.warn("parseLiteral, Unknown ref type: ${firstChild.elementType}")
                FrontMatterType.STRING(ref.text)
            }
        }
    }

    private fun parseFrontMatterValue(element: PsiElement): FrontMatterType? {
        when (element) {
            is ShireObjectKeyValue -> {
                val map: MutableMap<String, FrontMatterType> = mutableMapOf()
                element.children.mapNotNull {
                    if (it.elementType == ShireTypes.KEY_VALUE) {
                        processFrontmatter(it.children)
                    } else {
                        null
                    }
                }.forEach {
                    map.putAll(it)
                }

                return FrontMatterType.OBJECT(map)
            }
        }

        return when (element.firstChild.elementType) {
            ShireTypes.IDENTIFIER -> {
                FrontMatterType.IDENTIFIER(element.text)
            }

            ShireTypes.DATE -> {
                FrontMatterType.DATE(element.text)
            }

            ShireTypes.QUOTE_STRING -> {
                val value = element.text.substring(1, element.text.length - 1)
                FrontMatterType.STRING(value)
            }

            ShireTypes.NUMBER -> {
                FrontMatterType.NUMBER(element.text.toInt())
            }

            ShireTypes.BOOLEAN -> {
                FrontMatterType.BOOLEAN(element.text.toBoolean())
            }

            ShireTypes.FRONT_MATTER_ARRAY -> {
                val array: List<FrontMatterType> = parseArray(element)
                FrontMatterType.ARRAY(array)
            }

            ShireTypes.NEWLINE -> {
                return this.parseFrontMatterValue(element.firstChild.nextSibling)
            }

            ShireTypes.LBRACKET,
            ShireTypes.RBRACKET,
            ShireTypes.COMMA,
            WHITE_SPACE,
            null,
            -> {
                null
            }

            else -> {
                logger.warn("parseFrontMatterValue, Unknown frontmatter type: ${element.firstChild}")
                null
            }
        }
    }

    private fun parsePatternAction(element: PsiElement): FrontMatterType {
        val pattern = element.children.firstOrNull()?.text ?: ""

        val processor: MutableList<PatternActionFunc> = mutableListOf()
        val actionBlock = PsiTreeUtil.getChildOfType(element, ShireActionBlock::class.java)
        actionBlock?.actionBody?.actionExprList?.map { expr ->
            val funcCall = expr.funcCall
            val args = parseParameters(funcCall)

            when (funcCall?.funcName?.text) {
                "grep" -> {
                    if (args.isEmpty()) {
                        logger.warn("parsePatternAction, grep requires at least 1 argument")
                        return FrontMatterType.ERROR("grep requires at least 1 argument")
                    }

                    processor.add(PatternActionFunc.Grep(*args.toTypedArray()))
                }

                "sort" -> {
                    processor.add(PatternActionFunc.Sort(*args.toTypedArray()))
                }

                "sed" -> {
                    if (args.size < 2) {
                        logger.warn("parsePatternAction, sed requires at least 2 arguments")
                        return FrontMatterType.ERROR("sed requires at least 2 arguments")
                    }

                    if (args[0].startsWith("/") && args[0].endsWith("/")) {
                        processor.add(PatternActionFunc.Sed(args[0], args[1], true))
                    } else {
                        processor.add(PatternActionFunc.Sed(args[0], args[1]))
                    }
                }

                "xargs" -> {
                    processor.add(PatternActionFunc.Xargs(*args.toTypedArray()))
                }

                "uniq" -> {
                    processor.add(PatternActionFunc.Uniq(*args.toTypedArray()))
                }

                "head" -> {
                    if (args.isEmpty()) {
                        processor.add(PatternActionFunc.Head(10))
                    } else {
                        processor.add(PatternActionFunc.Head(args[0].toInt()))
                    }
                }

                "tail" -> {
                    if (args.isEmpty()) {
                        processor.add(PatternActionFunc.Tail(10))
                    } else {
                        processor.add(PatternActionFunc.Tail(args[0].toInt()))
                    }
                }

                "print" -> {
                    processor.add(PatternActionFunc.Print(*args.toTypedArray()))
                }

                "cat" -> {
                    processor.add(PatternActionFunc.Cat(*args.toTypedArray()))
                }

                else -> {
                    // remove surrounding quotes
                    val text = expr.text.removeSurrounding("\"")
                    processor.add(PatternActionFunc.Prompt(text))
                }
            }
        }

        return FrontMatterType.PATTERN(RuleBasedPatternAction(pattern, processor))
    }

    private fun parseParameters(funcCall: ShireFuncCall?): List<@NlsSafe String> =
        PsiTreeUtil.findChildOfType(funcCall, ShirePipelineArgs::class.java)
            ?.let {
                it.pipelineArgList.map { arg -> arg }
            }?.map {
                when (it.firstChild.elementType) {
                    ShireTypes.QUOTE_STRING -> it.text
                        .removeSurrounding("\"")
                        .removeSurrounding("'")

                    ShireTypes.IDENTIFIER -> it.text.removeSurrounding("\"")
                    else -> it.text
                }
            } ?: emptyList()

    private fun parseArray(element: PsiElement): List<FrontMatterType> {
        val array = mutableListOf<FrontMatterType>()
        var arrayElement: PsiElement? = element.children.firstOrNull()?.firstChild
        while (arrayElement != null) {
            parseFrontMatterValue(arrayElement)?.let {
                array.add(it)
            }
            arrayElement = arrayElement.nextSibling
        }

        return array
    }
}