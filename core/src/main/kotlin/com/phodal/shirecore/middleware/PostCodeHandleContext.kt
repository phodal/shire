package com.phodal.shirecore.middleware

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

class PostCodeHandleContext (
    /**
     * The element to be handled
     */
    val element: PsiElement,
    /**
     * The language of the code to be handled
     */
    val language: String,
    /**
     * Convert code to file
     */
    val file: VirtualFile? = null,
)