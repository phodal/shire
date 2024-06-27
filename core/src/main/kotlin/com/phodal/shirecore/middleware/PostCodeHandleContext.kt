package com.phodal.shirecore.middleware

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
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
    var currentLanguage: String?,

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
        private val DATA_KEY: Key<PostCodeHandleContext> = Key.create(PostCodeHandleContext::class.java.name)

        fun create(currentFile: PsiFile?,  selectedEntry: SelectedEntry?): PostCodeHandleContext {
            val language = currentFile?.language?.id

            return PostCodeHandleContext(
                selectedEntry = selectedEntry,
                currentFile = currentFile,
                currentLanguage = language,
            )
        }

        fun putData(context: PostCodeHandleContext) {
            UserDataHolderBase().putUserData(DATA_KEY, context)
        }
    }
}