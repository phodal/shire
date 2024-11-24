package com.phodal.shire.sonarqube

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.sonarlint.intellij.analysis.Analysis
import org.sonarlint.intellij.analysis.AnalysisCallback
import org.sonarlint.intellij.analysis.AnalysisResult
import org.sonarlint.intellij.analysis.AnalysisSubmitter
import org.sonarlint.intellij.tasks.startBackgroundableModalTask
import org.sonarlint.intellij.trigger.TriggerType
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

object SonarlintProvider {
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
                val result = StringBuilder()
                it.findings.issuesPerFile.forEach { (file, issues) ->
                    result.append("File: $file\n")
                    issues.forEach { issue ->
                        result.append("  -  ${issue.range}: ${issue.message}\n")
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

    private fun analysis(
        file: VirtualFile,
        project: Project,
        callback: (AnalysisResult) -> String,
    ): String? {
        val hasProject = Stream.of(file).anyMatch { f: VirtualFile -> f.path == project.basePath }
        if (hasProject) return null

        logger<SonarlintProvider>().info("Analysis file: ${file.path}")
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

        logger<SonarlintProvider>().info("Analysis file: ${file.path} finished")
        return future.get()
    }
}