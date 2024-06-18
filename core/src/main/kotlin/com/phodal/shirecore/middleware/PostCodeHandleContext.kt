package com.phodal.shirecore.middleware

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class PostCodeHandleContext (
    /**
     * The element to be handled
     */
    val element: PsiElement?,
    /**
     * The language of the code to be handled
     */
    val language: String?,
    /**
     * Convert code to file
     */
    val file: PsiFile? = null,
)