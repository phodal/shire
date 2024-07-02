package com.phodal.shirelang.run

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT
import com.intellij.psi.PsiElement
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.ShireActionBody
import com.phodal.shirelang.psi.ShireCaseBody
import com.phodal.shirelang.psi.ShireFrontMatterEntry
import com.phodal.shirelang.psi.ShireQueryStatement

class ShirePsiExprLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is ShireFrontMatterEntry) return null

        val key = element.frontMatterKey!!.firstChild

        val patternAction = element.patternAction
        if (patternAction != null) {
            val firstExpr = patternAction.actionBlock.actionBody.actionExprList.firstOrNull()
            when (firstExpr?.firstChild) {
                is ShireCaseBody -> {
                    return LineMarkerInfo(key, key.textRange, ShireIcons.Case, null, null, LEFT)
                    { "" }
                }
            }
        }


        when (element.functionStatement?.functionBody?.firstChild) {
            is ShireQueryStatement -> {
                return LineMarkerInfo(key, key.textRange, ShireIcons.PsiExpr, null, null, LEFT)
                { "" }
            }

            is ShireActionBody -> {
                return LineMarkerInfo(key, key.textRange, ShireIcons.Pipeline, null, null, LEFT)
                { "" }
            }
        }

        return null
    }
}
