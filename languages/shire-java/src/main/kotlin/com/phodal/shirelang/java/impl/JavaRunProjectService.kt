package com.phodal.shirelang.java.impl

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.JavaSdk
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.util.SmartList
import com.phodal.shirecore.provider.shire.ProjectRunService
import com.phodal.shirecore.runner.ConfigurationRunner
import com.phodal.shirelang.java.toolchain.GradleTasksUtil
import icons.GradleIcons

class JavaRunProjectService : ProjectRunService, ConfigurationRunner {
    override fun isAvailable(project: Project): Boolean {
        return ProjectRootManager.getInstance(project).projectSdk is JavaSdk
    }

    override fun run(project: Project, taskName: String) {
        val runConfiguration = GradleTasksUtil.configureGradleRun(project, taskName)
        executeRunConfigurations(project, runConfiguration)
    }

    override fun lookupAvailableTask(
        project: Project,
        parameters: CompletionParameters,
        result: CompletionResultSet,
    ): List<LookupElement> {
        val lookupElements: MutableList<LookupElement> = SmartList()
        GradleTasksUtil.collectGradleTasksData(project).filter {
            !it.isTest && !it.isJvmTest
        }.forEach {
            val element = LookupElementBuilder.create(it.getFqnTaskName())
                .withTypeText(it.description)
                .withIcon(GradleIcons.Gradle)

            lookupElements.add(PrioritizedLookupElement.withPriority(element, 99.0))
        }

        return lookupElements
    }

    override fun tasks(project: Project): List<String> {
        return GradleTasksUtil.collectGradleTasksData(project).map { it.getFqnTaskName() }
    }
}
