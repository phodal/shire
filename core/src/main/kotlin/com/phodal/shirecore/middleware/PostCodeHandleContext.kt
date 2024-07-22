package com.phodal.shirecore.middleware

import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.phodal.shirecore.middleware.select.SelectedEntry

class PostCodeHandleContext(
    /**
     * The element to be handled, which will be load from current editor when parse code
     */
    var selectedEntry: SelectedEntry? = null,

    /**
     * Convert code to file
     */
    var currentFile: PsiFile? = null,

    /**
     * The language of the code to be handled, which will parse from the GenText when parse code
     */
    var currentLanguage: Language? = null,

    /**
     * Convert code to file
     */
    var genTargetFile: PsiFile? = null,

    /**
     * Target Language
     */
    var genTargetLanguage: Language? = null,

    var genTargetExtension: String? = null,

    /**
     * The element to be handled, which will be load from current editor when parse code
     */
    var genPsiElement: PsiElement? = null,

    /**
     * The generated text to be handled
     */
    var genText: String? = null,

    /**
     * Parse from the [com.phodal.shirelang.compiler.hobbit.HobbitHole]
     */
    val currentParams: List<String>? = null,

    /**
     * The data to be passed to the post-processor
     */
    val pipeData: MutableMap<String, Any> = mutableMapOf(),

    /**
     * post text range
     */
    val modifiedTextRange: TextRange? = null,

    /**
     * current editor for modify
     */
    val editor: Editor? = null,

    var lastTaskOutput: String? = null,

    var compiledVariables: Map<String, Any> = mapOf(),
) {

    companion object {
        private val DATA_KEY: Key<PostCodeHandleContext> = Key.create(PostCodeHandleContext::class.java.name)
        private val userDataHolderBase = UserDataHolderBase()

        fun create(currentFile: PsiFile?,  selectedEntry: SelectedEntry?): PostCodeHandleContext {
            return PostCodeHandleContext(
                selectedEntry = selectedEntry,
                currentFile = currentFile,
                currentLanguage = currentFile?.language,
                editor = null,
            )
        }

        fun putData(context: PostCodeHandleContext) {
            userDataHolderBase.putUserData(DATA_KEY, context)
        }

        fun getData(): PostCodeHandleContext? {
            return userDataHolderBase.getUserData(DATA_KEY)
        }
    }
}