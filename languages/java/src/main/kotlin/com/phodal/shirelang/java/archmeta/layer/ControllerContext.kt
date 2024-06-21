package com.phodal.shirelang.java.archmeta.layer

import com.intellij.psi.PsiClass

data class ControllerContext(
    val services: List<PsiClass>,
    val models: List<PsiClass>,
    val repository: List<PsiClass> = listOf(),
)