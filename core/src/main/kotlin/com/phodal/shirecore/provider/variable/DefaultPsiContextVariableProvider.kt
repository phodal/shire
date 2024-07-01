package com.phodal.shirecore.provider.variable

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

class DefaultPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, psiElement: PsiElement?, editor: Editor): String {
        return ""
    }
}