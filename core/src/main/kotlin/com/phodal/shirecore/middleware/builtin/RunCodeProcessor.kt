package com.phodal.shirecore.middleware.builtin

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
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

    override fun isApplicable(context: PostCodeHandleContext): Boolean = true

    override fun execute(project: Project, context: PostCodeHandleContext, console: ConsoleView?, args: List<Any>): String {
        when (val code = context.pipeData["output"]) {
            is VirtualFile -> {
                LocalFileSystem.getInstance().refreshAndFindFileByPath(code.path)
                val psiFile = ReadAction.compute<PsiFile?, Throwable> {
                    PsiManager.getInstance(project).findFile(code)
                }

                psiFile?.let {
                    doExecute(console, project, code, it)
                    return ""
                }
            }

            is String -> {
                val ext = context.genTargetLanguage?.associatedFileType?.defaultExtension ?: "txt"
                ApplicationManager.getApplication().invokeAndWait {
                    PsiFileFactory.getInstance(project).createFileFromText("temp.$ext", code).let { psiFile ->
                        if (psiFile.virtualFile == null) {
                            console?.print("Failed to create file for run\n", ERROR_OUTPUT)
                        } else {
                            doExecute(console, project, psiFile.virtualFile, psiFile)
                        }
                    }
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
