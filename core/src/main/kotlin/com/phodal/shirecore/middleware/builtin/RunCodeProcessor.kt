package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.phodal.shirecore.middleware.BuiltinPostHandler
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.provider.shire.FileRunService

class RunCodeProcessor : PostProcessor {
    override val processorName: String = BuiltinPostHandler.RunCode.handleName

    override fun isApplicable(context: PostCodeHandleContext): Boolean {
        return true
    }

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): String {
        when (val code = context.pipeData["output"]) {
            is VirtualFile -> {
                LocalFileSystem.getInstance().refreshAndFindFileByPath(code.path)
                PsiManager.getInstance(project).findFile(code)?.let { psiFile ->
                    doExecute(console, project, code, psiFile)
                    return ""
                }
            }

            is String -> {
                val ext = context.genTargetLanguage?.associatedFileType?.defaultExtension ?: "txt"
                PsiFileFactory.getInstance(project).createFileFromText("temp.$ext", code).let { psiFile ->
                    val file = psiFile.virtualFile

                    if (file == null) {
                        console?.print("Failed to create file for run\n", ERROR_OUTPUT)
                        return ""
                    }

                    doExecute(console, project, file, psiFile)

                    return ""
                }
            }
        }

        console?.print("No code to run\n", ERROR_OUTPUT)
        return ""
    }

    private fun doExecute(
        console: ConsoleView?,
        project: Project,
        file: VirtualFile,
        psiFile: PsiFile,
    ) {
        val fileRunService = FileRunService.provider(project, file)
        if (fileRunService == null) {
            FileRunService.runInCli(project, psiFile)?.let {
                console?.print(it, NORMAL_OUTPUT)
                return
            }

            console?.print("No run service found\n", ERROR_OUTPUT)
            return
        }

        console?.print("Running code...\n", SYSTEM_OUTPUT)
        val output = fileRunService.runFile(project, file, psiFile)
        console?.print(output ?: "", NORMAL_OUTPUT)
    }
}
