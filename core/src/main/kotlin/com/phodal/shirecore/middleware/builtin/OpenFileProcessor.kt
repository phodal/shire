package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor

class OpenFileProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.OpenFile.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?): String {
        val file = context.pipeData["output"]
        if (file !is VirtualFile) {
            console?.print("No file to open\n", com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT)
            return ""
        }

        runInEdt {
            FileEditorManager.getInstance(project).openFile(file, true)
        }
        return ""
    }
}
