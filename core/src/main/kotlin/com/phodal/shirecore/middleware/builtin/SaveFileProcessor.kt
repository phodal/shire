package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.phodal.shirecore.SHIRE_TEMP_OUTPUT
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class SaveFileProcessor : PostProcessor {
    override val processorName: String get() = "saveFile"

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?): String {
        val language = context.targetLanguage ?: PlainTextLanguage.INSTANCE
        val ext = language?.associatedFileType?.defaultExtension ?: "txt"

        val outputFile = runWriteAction {
            val outputDir = project.guessProjectDir()?.findChild(SHIRE_TEMP_OUTPUT)
                ?: project.guessProjectDir()?.createChildDirectory(this, SHIRE_TEMP_OUTPUT)

            val outputFile = outputDir?.createChildData(this, "${System.currentTimeMillis()}.$ext")
            val content = context.pipeData["output"] as String?
            outputFile?.setBinaryContent(content?.toByteArray() ?: ByteArray(0))

            outputFile
        } ?: throw IllegalStateException("Failed to save file")

        context.pipeData["output"] = outputFile

        // refresh index
        project.guessProjectDir()?.refresh(true, true)

        console?.print("Saved to ${outputFile.canonicalPath}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        return outputFile.path ?: ""
    }
}
