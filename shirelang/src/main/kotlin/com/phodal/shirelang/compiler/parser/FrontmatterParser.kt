package com.phodal.shirelang.compiler.parser

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compiler.hobbit.*
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
        return runReadAction {
            PsiTreeUtil.getChildrenOfTypeAsList(file, ShireFrontMatterHeader::class.java).firstOrNull()?.let {
                parse(it)
            }
        }
    }

    private fun processFrontmatter(frontMatterEntries: Array<PsiElement>): MutableMap<String, FrontMatterType> {
        val frontMatter: MutableMap<String, FrontMatterType> = mutableMapOf()
        var lastKey = ""

        frontMatterEntries.forEach { entry ->
            entry.children.forEach { child ->
                when (child.elementType) {
                    ShireTypes.LIFECYCLE_ID,
                    ShireTypes.FRONT_MATTER_KEY,
                    -> {
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
                        frontMatter[lastKey] = parseLogicAndExprToType(child as ShireLogicalAndExpr)
                            ?: FrontMatterType.STRING("Logical expression parsing failed: ${child.text}")
                    }

                    ShireTypes.LOGICAL_OR_EXPR -> {
                        frontMatter[lastKey] = parseLogicOrExprToType(child as ShireLogicalOrExpr)
                            ?: FrontMatterType.STRING("Logical expression parsing failed: ${child.text}")
                    }

                    ShireTypes.CALL_EXPR -> {
                        parseExpr(child)?.let {
                            frontMatter[lastKey] = FrontMatterType.EXPRESSION(it)
                        }
                    }

                    ShireTypes.FUNCTION_STATEMENT -> {
                        frontMatter[lastKey] = parseFunction(child as ShireFunctionStatement)
                    }

                    /**
                     * For blocked when condition
                     *
                     * ```shire
                     * when: { $filePath.contains("src/main/java") && $fileName.contains(".java") }
                     * ```
                     */
                    ShireTypes.VARIABLE_EXPR -> {
                        // ignore
                        val childExpr = (child as ShireVariableExpr).expr
                        if (childExpr != null) {
                            parseExpr(childExpr)?.let {
                                frontMatter[lastKey] = FrontMatterType.EXPRESSION(it)
                            }
                        }
                    }

                    ShireTypes.LITERAL_EXPR -> {
                        parseExpr(child)?.let {
                            frontMatter[lastKey] = FrontMatterType.EXPRESSION(it)
                        }
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
        return when (val body = statement.functionBody?.firstChild) {
            is ShireQueryStatement -> {
                ShireAstQLParser.parse(body)
            }

            is ShireActionBody -> {
                val expressions = body.actionExprList.mapNotNull {
                    parseExpr(it)
                }.map {
                    FrontMatterType.EXPRESSION(it)
                }

                FrontMatterType.ARRAY(expressions)
            }

            null -> {
                FrontMatterType.EMPTY()
            }

            else -> {
                val expr = parseExpr(body)
                if (expr is Statement) {
                    return FrontMatterType.EXPRESSION(expr)
                }

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

        val leftStmt = parseExpr(left) ?: return null
        val rightStmt = parseExpr(right) ?: return null

        val logicalExpression = LogicalExpression(
            left = leftStmt,
            operator = OperatorType.And,
            right = rightStmt
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

        val leftStmt = parseExpr(left) ?: return null
        val rightStmt = parseExpr(right) ?: return null

        val logicOrExpr = LogicalExpression(
            left = leftStmt,
            operator = OperatorType.Or,
            right = rightStmt
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
    fun parseExpr(expr: PsiElement): Statement? = when (expr) {
        is ShireCallExpr -> {
            val expressionList = expr.expressionList
            val hasParentheses = expressionList?.prevSibling?.text == "("

            buildMethodCall(expr.refExpr, expressionList?.children, hasParentheses)
        }

        is ShireEqComparisonExpr -> {
            val variable = parseRefExpr(expr.children.firstOrNull())
            val value = parseRefExpr(expr.children.lastOrNull())
            Comparison(variable, Operator(OperatorType.Equal), value)
        }

        // INEQ_COMPARISON_EXPR
        is ShireIneqComparisonExpr -> {
            val variable = parseRefExpr(expr.children.firstOrNull())
            val value = parseRefExpr(expr.children.lastOrNull())
            val operatorType = OperatorType.fromString(expr.ineqComparisonOp.text)
            Comparison(variable, Operator(operatorType), value)
        }

        is ShireLogicalAndExpr -> {
            parseLogicAndExpr(expr)
                ?: Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }

        is ShireLogicalOrExpr -> {
            parseLogicOrExpr(expr)
                ?: Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }

        is ShireRefExpr -> {
            if (expr.expr == null) {
                Value(FrontMatterType.IDENTIFIER(expr.identifier.text))
            } else {
                val methodCall = buildMethodCall(expr, null, false)
                methodCall
            }
        }

        is ShireLiteralExpr -> {
            Value(parseLiteral(expr))
        }

        is ShireActionExpr -> {
            when (expr.firstChild) {
                is ShireFuncCall -> {
                    val args = parseParameters(expr.funcCall)
                    MethodCall(FrontMatterType.IDENTIFIER(expr.funcCall!!.funcName.text), FrontMatterType.EMPTY(), args)
                }

                is ShireCaseBody -> {
                    parseExprCaseBody(expr.firstChild as ShireCaseBody)
                }

                else -> {
                    logger.warn("parseExpr, Unknown action expression type: ${expr.firstChild.elementType}")
                    null
                }
            }
        }

        is ShireConditionStatement -> {
            val condition = parseLiteral(expr.caseCondition)
            val body = parseRefExpr(expr.expr)

            CaseKeyValue(condition, body as FrontMatterType.EXPRESSION)
        }

        else -> {
            logger.warn("parseExpr, Unknown expression type: ${expr.elementType}")
            null
        }
    }

    private fun parseExprCaseBody(caseBody: ShireCaseBody): ConditionCase? {
        val condition = caseBody.conditionFlag?.conditionStatementList?.mapNotNull {
            val condition = parseExpr(it)
            if (condition != null) {
                FrontMatterType.EXPRESSION(condition)
            } else {
                logger.warn("parseExprCaseBody, Unknown condition type: ${it.elementType}")
                null
            }
        } ?: emptyList()

        val body = caseBody.casePatternActionList.mapNotNull {
            val key = parseLiteral(it.caseCondition)
            val processor = parseActionBodyFuncCall(it.actionBody.actionExprList)
            FrontMatterType.EXPRESSION(CaseKeyValue(key, FrontMatterType.EXPRESSION(processor)))
        }

        return ConditionCase(condition, body)
    }

    private fun parseRefExpr(expr: PsiElement?): FrontMatterType {
        return when (expr) {
            is ShireLiteralExpr -> {
                parseLiteral(expr)
            }

            // fake refExpr ::= expr? '.' IDENTIFIER
            is ShireRefExpr -> {
                if (expr.expr == null) {
                    FrontMatterType.IDENTIFIER(expr.identifier.text)
                } else {
                    val methodCall = buildMethodCall(expr, null, false)
                    FrontMatterType.EXPRESSION(methodCall)
                }
            }

            is ShireCallExpr -> {
                val expressionList = expr.expressionList
                val hasParentheses = expressionList?.prevSibling?.text == "("

                val methodCall = buildMethodCall(expr.refExpr, expressionList?.children, hasParentheses)
                FrontMatterType.EXPRESSION(methodCall)
            }

            is ShireIneqComparisonExpr -> {
                val variable = parseRefExpr(expr.children.firstOrNull())
                val value = parseRefExpr(expr.children.lastOrNull())
                val operator = Operator(OperatorType.fromString(expr.ineqComparisonOp.text))

                val comparison = Comparison(variable, operator, value)
                FrontMatterType.EXPRESSION(comparison)
            }

            is ShireLogicalAndExpr -> {
                parseLogicAndExpr(expr)?.let {
                    FrontMatterType.EXPRESSION(it)
                } ?: FrontMatterType.ERROR("cannot parse ShireLogicalAndExpr: ${expr.text}")
            }

            is ShireLogicalOrExpr -> {
                parseLogicOrExpr(expr)?.let {
                    FrontMatterType.EXPRESSION(it)
                } ?: FrontMatterType.ERROR("cannot parse ShireLogicalOrExpr: ${expr.text}")
            }

            else -> {
                logger.warn("parseRefExpr, Unknown expression type: ${expr?.elementType}")
                FrontMatterType.STRING("")
            }
        }
    }


    private fun buildMethodCall(
        refExpr: ShireRefExpr,
        expressionList: Array<PsiElement>?,
        hasParentheses: Boolean,
    ): MethodCall {
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

            ShireTypes.DEFAULT -> {
                FrontMatterType.IDENTIFIER(ref.text)
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
                return parseFrontMatterValue(element.firstChild.nextSibling)
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

    private fun parsePatternAction(element: PsiElement): FrontMatterType? {
        val pattern = element.children.firstOrNull()?.text ?: return null

        val actionBlock = PsiTreeUtil.getChildOfType(element, ShireActionBlock::class.java)
        val actionBody = actionBlock?.actionBody ?: return null

        val processor: List<PatternActionFunc> = parseActionBodyFuncCall(actionBody.actionExprList).processors
        return FrontMatterType.PATTERN(RuleBasedPatternAction(pattern, processor))
    }

    private fun parseActionBodyFuncCall(shireActionExprs: List<ShireActionExpr>?): Processor {
        val processor: MutableList<PatternActionFunc> = mutableListOf()
        shireActionExprs?.forEach { expr ->
            expr.funcCall?.let { funcCall ->
                parseActionBodyFunCall(funcCall, expr)?.let {
                    processor.add(it)
                }
            }
            expr.caseBody?.let { caseBody ->
                parseExprCaseBody(caseBody)?.let { conditionCase ->
                    val cases = conditionCase.cases.map {
                        (it as FrontMatterType.EXPRESSION).value as CaseKeyValue
                    }

                    processor.add(PatternActionFunc.CaseMatch(cases))
                }
            }
        }


        return Processor(processor)
    }

    fun parseActionBodyFunCall(funcCall: ShireFuncCall?, expr: ShireActionExpr): PatternActionFunc? {
        val args = parseParameters(funcCall) ?: emptyList()
        val patternActionFunc = when (funcCall?.funcName?.text) {
            "grep" -> {
                if (args.isEmpty()) {
                    logger.error("parsePatternAction, grep requires at least 1 argument")
                    return null
                }

                PatternActionFunc.Grep(*args.toTypedArray())
            }

            "sort" -> {
                PatternActionFunc.Sort(*args.toTypedArray())
            }

            "sed" -> {
                if (args.size < 2) {
                    logger.error("parsePatternAction, sed requires at least 2 arguments")
                    return null
                }

                if (args[0].startsWith("/") && args[0].endsWith("/")) {
                    PatternActionFunc.Sed(args[0], args[1], true)
                } else {
                    PatternActionFunc.Sed(args[0], args[1])
                }
            }

            "xargs" -> {
                PatternActionFunc.Xargs(*args.toTypedArray())
            }

            "uniq" -> {
                PatternActionFunc.Uniq(*args.toTypedArray())
            }

            "head" -> {
                if (args.isEmpty()) {
                    PatternActionFunc.Head(10)
                } else {
                    PatternActionFunc.Head(args[0].toInt())
                }
            }

            "tail" -> {
                if (args.isEmpty()) {
                    PatternActionFunc.Tail(10)
                } else {
                    PatternActionFunc.Tail(args[0].toInt())
                }
            }

            "print" -> {
                PatternActionFunc.Print(*args.toTypedArray())
            }

            "cat" -> {
                PatternActionFunc.Cat(*args.toTypedArray())
            }

            /// System APIs
            "execute" -> {
                PatternActionFunc.ExecuteShire(args[0])
            }

            "notify" -> {
                PatternActionFunc.Notify(args[0])
            }

            "embedding" -> {
                PatternActionFunc.Embedding(args[0])
            }

            "splitting" -> {
                PatternActionFunc.Splitting(args.toTypedArray())
            }

            null -> {
                logger.warn("parsePatternAction, Unknown pattern action: ${expr.funcCall}")
                return null
            }

            else -> {
                val funcName = funcCall.funcName.text ?: ""
                PatternActionFunc.UserCustom(funcName, args)
            }
        }
        return patternActionFunc
    }

    private fun parseParameters(funcCall: ShireFuncCall?): List<String>? = runReadAction {
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
            }
    }

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