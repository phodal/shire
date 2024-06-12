package com.phodal.shirelang.compiler

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compiler.hobbit.*
import com.phodal.shirelang.compiler.hobbit.ShirePatternAction
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
                        frontMatter[lastKey] = parseLogicAndExpr(child as ShireLogicalAndExpr)
                            ?: FrontMatterType.STRING("Logical expression parsing failed: ${child.text}")
                    }

                    else -> {
                        logger.warn("Unknown frontmatter type: ${child.elementType}")
                    }
                }
            }
        }

        return frontMatter
    }

    private fun parseLogicAndExpr(child: ShireLogicalAndExpr): FrontMatterType? {
        val left = child.exprList.firstOrNull() ?: return null
        val right = child.exprList.lastOrNull() ?: return null

        return FrontMatterType.Expression(
            LogicalExpression(
                left = parseExpr(left),
                operator = OperatorType.And,
                right = parseExpr(right)
            )
        )
    }

    private fun parseExpr(expr: PsiElement): Statement = when (expr.elementType) {
        ShireTypes.EQ_COMPARISON_EXPR -> {
            val variable = parseRefExpr(expr.children.firstOrNull())
            val value = parseRefExpr(expr.children.lastOrNull())
            Comparison(variable, Operator(OperatorType.Equal), value)
        }

        else -> {
            logger.warn("parseExpr, Unknown expression type: ${expr.elementType}")
            Comparison(FrontMatterType.STRING(""), Operator(OperatorType.Equal), FrontMatterType.STRING(""))
        }
    }

    private fun parseRefExpr(expr: PsiElement?): FrontMatterType {
        return when (expr?.elementType) {
            ShireTypes.LITERAL_EXPR -> {
                FrontMatterType.STRING(expr!!.text)
            }

            ShireTypes.REF_EXPR -> {
                val ref = expr!!.children.firstOrNull()!!
                when (ref?.elementType) {
                    ShireTypes.IDENTIFIER -> {
                        FrontMatterType.STRING(ref.text)
                    }

                    ShireTypes.NUMBER -> {
                        FrontMatterType.NUMBER(ref.text.toInt())
                    }

                    ShireTypes.QUOTE_STRING -> {
                        val value = ref.text.substring(1, ref.text.length - 1)
                        FrontMatterType.STRING(value)
                    }

                    else -> {
                        logger.warn("parseRefExpr, Unknown ref type: ${ref?.elementType}")
                        FrontMatterType.STRING("")
                    }
                }
            }

            ShireTypes.CALL_EXPR -> {
                val childrens = PsiTreeUtil.findChildrenOfType(expr, ShireExpr::class.java)
                val left = parseRefExpr(childrens.first())
                // next will be dot
                val right = parseRefExpr(childrens.last())

                val methodCall = MethodCall(left, right, listOf())
                FrontMatterType.Expression(methodCall)
            }

            else -> {
                logger.warn("parseRefExpr, Unknown expression type: ${expr?.elementType}")
                FrontMatterType.STRING("")
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
                FrontMatterType.STRING(element.text)
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
                logger.warn("Unknown frontmatter type: ${element.firstChild}")
                null
            }
        }
    }

    private fun parsePatternAction(element: PsiElement): FrontMatterType? {
        val pattern = element.children.firstOrNull()?.text ?: ""

        val processor: MutableList<PatternFun> = mutableListOf()
        val actionBlock = PsiTreeUtil.getChildOfType(element, ShireActionBlock::class.java)
        actionBlock?.actionBody?.actionExprList?.map { expr ->
            val funcCall = expr.funcCall
            val args = parseParameters(funcCall)

            when (funcCall?.funcName?.text) {
                "grep" -> {
                    processor.add(PatternFun.Grep(*args.toTypedArray()))
                }

                "sort" -> {
                    processor.add(PatternFun.Sort(*args.toTypedArray()))
                }

                "sed" -> {
                    processor.add(PatternFun.Sed(args[0], args[1]))
                }

                "xargs" -> {
                    processor.add(PatternFun.Xargs(*args.toTypedArray()))
                }

                "uniq" -> {
                    processor.add(PatternFun.Uniq(*args.toTypedArray()))
                }

                "head" -> {
                    processor.add(PatternFun.Head(args[0].toInt()))
                }

                "tail" -> {
                    processor.add(PatternFun.Tail(args[0].toInt()))
                }

                "print" -> {
                    processor.add(PatternFun.Print(*args.toTypedArray()))
                }

                else -> {
                    // remove surrounding quotes
                    val text = expr.text.removeSurrounding("\"")
                    processor.add(PatternFun.Prompt(text))
                }
            }
        }

        return FrontMatterType.PATTERN(ShirePatternAction(pattern, processor))
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