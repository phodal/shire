package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.SHIRE_TEMP_OUTPUT
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class SaveFileProcessor : PostProcessor, Disposable {
    override val processorName: String = BuiltinPostHandler.SaveFile.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): String {
        val fileName = if (args.isNotEmpty()) {
            args[0].toString()
        } else {
            val language = context.genTargetLanguage ?: PlainTextLanguage.INSTANCE
            val ext = context.genTargetExtension ?: language?.associatedFileType?.defaultExtension ?: "txt"

            "${System.currentTimeMillis()}.$ext"
        }

        ApplicationManager.getApplication().invokeAndWait {
            WriteAction.compute<VirtualFile, Throwable> {
                val outputDir = project.guessProjectDir()?.findChild(SHIRE_TEMP_OUTPUT)
                    ?: project.guessProjectDir()?.createChildDirectory(this, SHIRE_TEMP_OUTPUT)

                val outputFile = outputDir?.createChildData(this, fileName)
                    ?: throw IllegalStateException("Failed to save file")

                val content = context.pipeData["output"] as String?
                outputFile.setBinaryContent(content?.toByteArray() ?: ByteArray(0))
                context.pipeData["output"] = outputFile

                project.guessProjectDir()?.refresh(true, true)

                console?.print("Saved to ${outputFile.canonicalPath}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
                outputFile
            }
        }

        return ""
    }

    override fun dispose() {

    }
}

