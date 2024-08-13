package com.phodal.shire.sonarqube

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ex.ActionUtil.invokeAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.treeStructure.Tree
import org.sonarlint.intellij.analysis.Analysis
import org.sonarlint.intellij.analysis.AnalysisCallback
import org.sonarlint.intellij.analysis.AnalysisResult
import org.sonarlint.intellij.analysis.AnalysisSubmitter
import org.sonarlint.intellij.cayc.CleanAsYouCodeService
import org.sonarlint.intellij.common.util.SonarLintUtils
import org.sonarlint.intellij.finding.issue.LiveIssue
import org.sonarlint.intellij.tasks.startBackgroundableModalTask
import org.sonarlint.intellij.trigger.TriggerType
import org.sonarlint.intellij.ui.CurrentFilePanel
import org.sonarlint.intellij.ui.tree.IssueTree
import org.sonarlint.intellij.ui.tree.IssueTreeModelBuilder
import org.sonarlint.intellij.util.SonarLintActions
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import java.util.stream.Stream

object SonarlintProvider {
    private val analyzeCurrentFileAction: AnAction = SonarLintActions.getInstance().analyzeCurrentFileAction()

    fun analysisFile(project: Project, file: VirtualFile): String {
        val hasProject = Stream.of(file).anyMatch { f: VirtualFile -> f.path == project.basePath }
        if (hasProject) {
            return "Project path is same as file path"
        }

        logger<SonarlintProvider>().info("Analysis file: ${file.path}")
        val future = CompletableFuture<String>()
        val callback: AnalysisCallback = object : AnalysisCallback {
            override fun onSuccess(analysisResult: AnalysisResult) {
                val result = StringBuilder()
                analysisResult.findings.issuesPerFile.forEach { (file, issues) ->
                    result.append("File: $file\n")
                    issues.forEach { issue ->
                        result.append("  - ${issue.userSeverity}: ${issue.message}\n")
                    }
                }

                future.complete(result.toString())
            }

            override fun onError(p0: Throwable) {
                future.completeExceptionally(p0)
            }

        }

        val analysis = Analysis(project, listOf(file), TriggerType.CURRENT_FILE_ACTION, callback)
        startBackgroundableModalTask(
            project, AnalysisSubmitter.ANALYSIS_TASK_TITLE
        ) { indicator: ProgressIndicator? ->
            if (indicator != null) {
                analysis.run(indicator)
            }
        }

        logger<SonarlintProvider>().info("Analysis file: ${file.path} finished")
        return future.get()
    }


    fun getSonarResult(project: Project, file: VirtualFile): String {
        var treeBuilder: IssueTreeModelBuilder = IssueTreeModelBuilder(project)
        val model = treeBuilder.createModel(false)
        val tree: Tree = IssueTree(project, model)

        val statusText = StringBuilder()
        val dataContext: DataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(1000)
            ?: return statusText.toString()
        val templateText = analyzeCurrentFileAction.templateText
        if (templateText != null) {
            ActionListener { ignore: ActionEvent? ->
                invokeAction(
                    analyzeCurrentFileAction,
                    dataContext, CurrentFilePanel.SONARLINT_TOOLWINDOW_ID, null, null
                )
            }
        }

        val currentIssues: Collection<LiveIssue> = listOf()

        if (SonarLintUtils.getService(CleanAsYouCodeService::class.java).shouldFocusOnNewCode(project)
        ) {
            val oldIssues: List<LiveIssue> =
                currentIssues.stream().filter(Predicate.not<LiveIssue> { obj: LiveIssue -> obj.isOnNewCode() })
                    .toList()
            val newIssues: List<LiveIssue> =
                currentIssues.stream().filter { obj: LiveIssue -> obj.isOnNewCode() }.toList()

        }

        return statusText.toString()
    }
}