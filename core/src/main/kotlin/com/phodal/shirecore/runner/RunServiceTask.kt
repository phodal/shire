// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.phodal.shirecore.runner

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.AnsiEscapeDecoder
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.testframework.Filter
import com.intellij.execution.testframework.sm.runner.SMTRunnerEventsAdapter
import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.util.text.nullize
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.provider.FileRunService

open class RunServiceTask(
    private val project: Project,
    private val virtualFile: VirtualFile,
    private val testElement: PsiElement?,
    private val fileRunService: FileRunService,
    private val runner: ProgramRunner<*>? = null,
) : ConfigurationRunner, com.intellij.openapi.progress.Task.Backgroundable(
    project, ShireCoreBundle.message("progress.run.task"), true
) {
    override fun runnerId() = runner?.runnerId ?: DefaultRunExecutor.EXECUTOR_ID

    override fun run(indicator: ProgressIndicator) {
        runAndCollectTestResults(indicator)
    }

    /**
     * This function is responsible for executing a run configuration and returning the corresponding check result.
     * It is used within the test framework to run tests and report the results back to the user.
     *
     * @param indicator A progress indicator that is used to track the progress of the execution.
     * @return The check result of the executed run configuration, or `null` if no run configuration could be created.
     */
    private fun runAndCollectTestResults(indicator: ProgressIndicator?): RunnerResult? {
        val settings: RunnerAndConfigurationSettings? =
            fileRunService.createRunSettings(project, virtualFile, testElement)
        if (settings == null) {
            logger<RunServiceTask>().warn("No run configuration found for file: ${virtualFile.path}")
            return null
        }

        settings.isActivateToolWindowBeforeRun = false

        val testRoots = mutableListOf<SMTestProxy.SMRootTestProxy>()
        val testEventsListener = object : SMTRunnerEventsAdapter() {
            override fun onTestingStarted(testsRoot: SMTestProxy.SMRootTestProxy) {
                testRoots += testsRoot
            }
        }

        val runContext = createRunContext()
        executeRunConfigures(project, settings, runContext, testEventsListener, indicator)

        @Suppress("UnstableApiUsage")
        invokeAndWaitIfNeeded { }

        val testResults = testRoots.mapNotNull { it.toCheckResult() }
        if (testResults.isEmpty()) return RunnerResult.noTestsRun

        val firstFailure = testResults.firstOrNull { it.status != RunnerStatus.Solved }
        val result = firstFailure ?: testResults.first()
        return result
    }

    private fun SMTestProxy.SMRootTestProxy.toCheckResult(): RunnerResult? {
        if (finishedSuccessfully()) return RunnerResult(RunnerStatus.Solved, "CONGRATULATIONS")

        val failedChildren = collectChildren(object : Filter<SMTestProxy>() {
            override fun shouldAccept(test: SMTestProxy): Boolean = test.isLeaf && !test.finishedSuccessfully()
        })

        val firstFailedTest = failedChildren.firstOrNull()
        if (firstFailedTest == null) {
            ShirelangNotifications.warn(project, "Testing failed although no failed tests found")
            return null
        }

        val diff = firstFailedTest.diffViewerProvider?.let {
            CheckResultDiff(it.left, it.right, it.diffTitle)
        }
        val message = if (diff != null) getComparisonErrorMessage(firstFailedTest) else getErrorMessage(firstFailedTest)
        val details = firstFailedTest.stacktrace
        return RunnerResult(
            RunnerStatus.Failed,
            removeAttributes(fillWithIncorrect(message)),
            diff = diff,
            details = details
        )
    }

    private fun SMTestProxy.finishedSuccessfully(): Boolean {
        return !hasErrors() && (isPassed || isIgnored)
    }

    /**
     * Some testing frameworks add attributes to be shown in console (ex. Jest - ANSI color codes)
     * which are not supported in Task Description, so they need to be removed
     */
    private fun removeAttributes(text: String): String {
        val buffer = StringBuilder()
        AnsiEscapeDecoder().escapeText(text, ProcessOutputTypes.STDOUT) { chunk, _ ->
            buffer.append(chunk)
        }
        return buffer.toString()
    }

    /**
     * Returns message for test error that will be shown to a user in Check Result panel
     */
    @Suppress("UnstableApiUsage")
    @NlsSafe
    private fun getErrorMessage(node: SMTestProxy): String = node.errorMessage ?: "Execution failed"

    /**
     * Returns message for comparison error that will be shown to a user in Check Result panel
     */
    private fun getComparisonErrorMessage(node: SMTestProxy): String = getErrorMessage(node)

    private fun fillWithIncorrect(message: String): String =
        message.nullize(nullizeSpaces = true) ?: "Incorrect"
}