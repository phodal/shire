package com.phodal.shire.sonarqube

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import org.sonarlint.intellij.analysis.Analysis
import org.sonarlint.intellij.analysis.AnalysisCallback
import org.sonarlint.intellij.analysis.AnalysisResult
import org.sonarlint.intellij.analysis.AnalysisSubmitter
import org.sonarlint.intellij.tasks.startBackgroundableModalTask
import org.sonarlint.intellij.trigger.TriggerType
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

object SonarLintProvider {
    fun analysisFile(project: Project, file: VirtualFile): String? {
        return analysis(file, project) {
            ReadAction.compute<String, Throwable> {
                val result = StringBuilder()
                it.findings.issuesPerFile.forEach { (file, issues) ->
                    result.append("File: $file\n")
                    issues.forEach { issue ->
                        result.append("  - ${issue.userSeverity}: ${issue.message}\n")
                    }
                }

                result.toString()
            }
        }
    }

    fun analysisResults(project: Project, file: VirtualFile): String? {
        return analysis(file, project) {
            ReadAction.compute<String, Throwable> {
                val psiFile = PsiManager.getInstance(project).findFile(file) ?: return@compute ""
                val document = PsiDocumentManager.getInstance(project).getDocument(psiFile) ?: return@compute ""

                val result = StringBuilder()
                it.findings.securityHotspotsPerFile.forEach { (file, issues) ->
                    result.append("File: $file\n")
                    issues.forEach { issue ->
                        val range = issue.validTextRange
                        val lineContent: String = if (range != null) {
                            val (startLine, endLine) = textRangeToLineNumbers(document, range)
                            "($startLine-$endLine) ${document.getText(range)}"
                        } else {
                            ""
                        }

                        result.append("  -  ${lineContent} has issue : ${issue.message}\n")
                        if (issue.quickFixes().isNotEmpty()) {
                            result.append("    Quick Fixes suggestion: ")
                            issue.quickFixes().forEach { quickFix ->
                                result.append("      - ${quickFix.message}\n")
                            }
                        }
                    }
                }

                result.toString()
            }
        }
    }

    private fun textRangeToLineNumbers(document: Document, textRange: TextRange): Pair<Int, Int> {
        val startLine = document.getLineNumber(textRange.startOffset) + 1
        val endLine = document.getLineNumber(textRange.endOffset) + 1
        return startLine to endLine
    }

    private fun analysis(
        file: VirtualFile,
        project: Project,
        callback: (AnalysisResult) -> String,
    ): String? {
        val hasProject = Stream.of(file).anyMatch { f: VirtualFile -> f.path == project.basePath }
        if (hasProject) return null

        logger<SonarLintProvider>().info("Analysis file: ${file.path}")
        val future = CompletableFuture<String>()
        val analysis = Analysis(project, listOf(file), TriggerType.CURRENT_FILE_ACTION, object : AnalysisCallback {
            override fun onSuccess(analysisResult: AnalysisResult) {
                future.complete(callback(analysisResult))
            }

            override fun onError(throwable: Throwable) {
                future.completeExceptionally(throwable)
            }
        })

        startBackgroundableModalTask(project, AnalysisSubmitter.ANALYSIS_TASK_TITLE) { indicator: ProgressIndicator? ->
            if (indicator != null) {
                analysis.run(indicator)
            }
        }

        logger<SonarLintProvider>().info("Analysis file: ${file.path} finished")
        return future.get()
    }
}