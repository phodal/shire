package com.phodal.shirelang.compiler

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.PatternFun
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
                        val value = parseFrontMatterValue(child)
                        value?.let {
                            frontMatter[lastKey] = it
                        }
                    }

                    ShireTypes.PATTERN_ACTION -> {
                        val value = parsePatternAction(child)
                        value?.let {
                            frontMatter[lastKey] = it
                        }
                    }

                    else -> {
                        logger.warn("Unknown frontmatter type: ${child.elementType}")
                    }
                }
            }
        }

        return frontMatter
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
            ShireTypes.STRING -> {
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

    private fun parsePatternAction(element: PsiElement): FrontMatterType {
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

                "replace" -> {
                    processor.add(PatternFun.Replace(args[0], args[1]))
                }

                "xargs" -> {
                    processor.add(PatternFun.Xargs(*args.toTypedArray()))
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
                when(it.firstChild.elementType) {
                    ShireTypes.QUOTE_STRING -> it.text
                        .removeSurrounding("\"")
                        .removeSurrounding("'")
                    ShireTypes.STRING -> it.text.removeSurrounding("\"")
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