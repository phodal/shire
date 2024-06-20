package com.phodal.shirecore.provider.variable

import com.intellij.psi.PsiElement

class DefaultPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement?, variable: PsiContextVariable): String {
        return ""
    }
}