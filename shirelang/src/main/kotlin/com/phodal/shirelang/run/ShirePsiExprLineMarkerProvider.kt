package com.phodal.shirelang.run

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.ShireFrontMatterEntry

class ShirePsiExprLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element !is ShireFrontMatterEntry) return null

        if (element.functionStatement == null) {
            return null
        }

        if (element.functionStatement?.functionBody?.queryStatement == null) {
            return null
        }

        val firstChildKeyElement = element.frontMatterKey!!.firstChild

        return LineMarkerInfo(
            firstChildKeyElement,
            firstChildKeyElement.textRange,
            ShireIcons.PsiExpr,
            null,
            null,
            GutterIconRenderer.Alignment.LEFT
        ) {
            ""
        }
    }
}
