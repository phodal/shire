package com.phodal.shirelang.compiler

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.WHITE_SPACE
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.FrontMatterType
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireFrontMatterHeader
import com.phodal.shirelang.psi.ShireTypes

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
                }
            }
        }

        return frontMatter
    }

    private fun parseFrontMatterValue(element: PsiElement): FrontMatterType? {
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

            ShireTypes.LBRACKET, ShireTypes.RBRACKET, ShireTypes.COMMA, WHITE_SPACE -> {
                null
            }

            null -> {
                null
            }

            else -> {
                logger.warn("Unknown frontmatter type: ${element.elementType}")
                null
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