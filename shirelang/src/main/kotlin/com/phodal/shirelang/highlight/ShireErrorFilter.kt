package com.phodal.shirelang.highlight

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.phodal.shirelang.ShireLanguage
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile

class ShireErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        val containingFile: PsiFile = element.containingFile
        return !(containingFile.language === ShireLanguage.INSTANCE && containingFile.context != null)
    }
}
