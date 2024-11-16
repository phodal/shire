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
import com.phodal.shirecore.middleware.PostProcessorType
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.provider.shire.FileRunService

class RunCodeProcessor : PostProcessor {
    override val processorName: String = PostProcessorType.RunCode.handleName
    override val description: String = "`runCode` will run the code, default will be test file."

    override fun isApplicable(context: PostProcessorContext): Boolean = true

    override fun execute(
        project: Project,
        context: PostProcessorContext,
        console: ConsoleView?,
        args: List<Any>,
    ): String {
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
                    if (code.contains("\n")) {
                        PsiFileFactory.getInstance(project).createFileFromText("temp.$ext", code).let { psiFile ->
                            if (psiFile.virtualFile == null) {
                                console?.print("Failed to create file for run\n", ERROR_OUTPUT)
                            } else {
                                doExecute(console, project, psiFile.virtualFile, psiFile)
                            }
                        }
                    } else {
                        val file = LocalFileSystem.getInstance().refreshAndFindFileByPath(code)
                        if (file != null) {
                            val psiFile = ReadAction.compute<PsiFile?, Throwable> {
                                PsiManager.getInstance(project).findFile(file)
                            }

                            psiFile?.let {
                                doExecute(console, project, file, psiFile)
                            }
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
            val cliResult = FileRunService.runInCli(project, psiFile)
            if (cliResult != null) {
                console?.print(cliResult, NORMAL_OUTPUT)
                return
            }

            FileRunService.retryRun(project, file)?.let {
                console?.print(it, NORMAL_OUTPUT)
                return
            }

            console?.print("RunCode: No run service found for file: $file\n", ERROR_OUTPUT)
            return
        }

        console?.print("Running code...\n", SYSTEM_OUTPUT)
        val output = fileRunService.runFile(project, file, psiFile)
        console?.print(output ?: "", NORMAL_OUTPUT)
    }
}
