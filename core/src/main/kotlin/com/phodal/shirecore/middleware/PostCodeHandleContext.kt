package com.phodal.shirecore.middleware

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

class PostCodeHandleContext(
    /**
     * The element to be handled, which will be load from current editor when parse code
     */
    var currentElement: PsiElement?,

    /**
     * Convert code to file
     */
    var currentFile: PsiFile? = null,

    /**
     * The language of the code to be handled, which will parse from the GenText when parse code
     */
    var language: String?,

    /**
     * Convert code to file
     */
    var targetFile: PsiFile? = null,

    /**
     * The generated text to be handled
     */
    val genText: String? = null,

    /**
     * Parse from the [com.phodal.shirelang.compiler.hobbit.HobbitHole]
     */
    val currentParams: List<String>? = null,

    /**
     * The data to be passed to the post-processor
     */
    val pipeData: MutableMap<String, Any> = mutableMapOf(),
) {
    companion object {
        fun create(file: PsiFile?, language: @NlsSafe String?, editor: Editor?): PostCodeHandleContext {
            return PostCodeHandleContext(
                currentElement = null,
                currentFile = file,
                language = language,
            )
        }
    }
}