package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.filters.FileHyperlinkInfoBase
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor


class OpenFileProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.OpenFile.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): String {
        val file = context.pipeData["output"] ?: context.genText
        if (file !is VirtualFile) {
            if (file is String) {
                // check has multiple files
                val files = file.split("\n")
                runInEdt {
                    val findFiles = files.mapNotNull { project.findFile(it) }
                    findFiles.map {
                        console?.printHyperlink("$it", OpenFileHyperlinkInfo(project, it, -1, -1))
                    }

                    if (findFiles.isNotEmpty()) {
                        FileEditorManager.getInstance(project).openFile(findFiles.first(), true)
                    }
                }

                return ""
            } else {
                console?.print("No file to open\n", com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT)
            }

            return ""
        }

        runInEdt {
            FileEditorManager.getInstance(project).openFile(file, true)
        }

        return ""
    }
}

fun Project.findFile(path: String): VirtualFile? {
    ApplicationManager.getApplication().assertReadAccessAllowed()
    val searchScope = ProjectScope.getProjectScope(this)
    val fileType: FileType = FileTypeManager.getInstance().getFileTypeByFileName(path)
    val allTypeFiles = FileTypeIndex.getFiles(fileType, searchScope)

    for (file in allTypeFiles) {
        if (file.name == path || file.path.endsWith(path)) {
            return file
        }
    }

    return null
}
