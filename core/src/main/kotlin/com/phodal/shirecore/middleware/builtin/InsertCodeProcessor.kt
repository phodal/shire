package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.codeedit.CodeModifier
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessor

class InsertCodeProcessor : PostProcessor {
    override val processorName: String = PostProcessorType.InsertCode.handleName

    override fun isApplicable(context: PostProcessorContext): Boolean = true

    override fun execute(project: Project, context: PostProcessorContext, console: ConsoleView?, args: List<Any>): String {
        if (context.currentLanguage == null || context.currentFile == null) {
            console?.print("No found current language\n", com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT)
            return ""
        }


        val codeModifier = CodeModifier.forLanguage(context.currentLanguage!!)
        if (codeModifier == null) {
            console?.print("No code modifier found\n", com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT)
            return ""
        }

        context.genPsiElement = codeModifier.smartInsert(context.currentFile!!.virtualFile!!, project, context.genText ?: "")
        return ""
    }
}
