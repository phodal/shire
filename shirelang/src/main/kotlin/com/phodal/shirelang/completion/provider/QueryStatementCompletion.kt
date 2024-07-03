package com.phodal.shirelang.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class QueryStatementCompletion : CompletionProvider<CompletionParameters>() {
    /** will auto insert code
     * ```shire
     *   from {
     *     PsiClass clazz / * sample * /
     *   }
     *   where {
     *     // your code here
     *   }
     *   select {
     *     // output selection
     *   }
     * ```
     */
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        // 添加代码补全项
        result.addElement(
            LookupElementBuilder.create("from { ... } where { ... } select { ... }")
                .withInsertHandler { context, item ->
                    val document = context.document
                    val startOffset = context.startOffset
                    val endOffset = context.tailOffset

                    document.replaceString(startOffset, endOffset, "from {\n} where {\n} select {\n}")

                    // 移动光标到 from 后面
                    context.editor.caretModel.moveToOffset(startOffset + 5)
                }
        )
    }
}
