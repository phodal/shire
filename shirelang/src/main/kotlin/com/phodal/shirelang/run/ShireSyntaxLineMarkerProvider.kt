package com.phodal.shirelang.run

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.*

class ShireSyntaxLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is ShireFrontMatterEntry) return null

        // only leaf elements can have line markers, or IDEA will throw an exception
        val leafElement = getLeafElement(element) ?: return null

        val patternAction = element.patternAction
        if (patternAction != null) {
            val firstExpr = patternAction.actionBlock.actionBody.actionExprList.firstOrNull()
            when (firstExpr?.firstChild) {
                is ShireCaseBody -> {
                    return LineMarkerInfo(leafElement, leafElement.textRange, ShireIcons.Case, null, null, LEFT)
                    { "" }
                }
            }
        }


        when (element.functionStatement?.functionBody?.firstChild) {
            is ShireQueryStatement -> {
                return LineMarkerInfo(leafElement, leafElement.textRange, ShireIcons.PsiExpr, null, null, LEFT)
                { "" }
            }

            is ShireActionBody -> {
                return LineMarkerInfo(leafElement, leafElement.textRange, ShireIcons.Pipeline, null, null, LEFT)
                { "" }
            }
        }

        return null
    }

    private fun getLeafElement(element: ShireFrontMatterEntry): PsiElement? {
        val firstChild = element.frontMatterKey?.firstChild
        when (firstChild?.elementType) {
            ShireTypes.PATTERN,
            ShireTypes.FRONT_MATTER_ID -> {
                return firstChild?.firstChild ?: firstChild
            }
            ShireTypes.IDENTIFIER -> {
                return firstChild
            }
        }

        return firstChild
    }
}
