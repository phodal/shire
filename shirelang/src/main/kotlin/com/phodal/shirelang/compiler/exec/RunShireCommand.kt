package com.phodal.shirelang.compiler.exec

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.AutoTesting
import com.phodal.shirecore.provider.ProjectRunService
import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.phodal.shirelang.utils.lookupFile

/**
 * The `RunAutoCommand` class is responsible for executing an auto command on a given project.
 *
 * @property myProject The project to execute the auto command on.
 * @property argument The name of the file to find and run tests for.
 *
 */
class RunShireCommand(val myProject: Project, private val argument: String) : ShireCommand {
    override suspend fun doExecute(): String {
        val task = ProjectRunService.all().mapNotNull { projectRun ->
            val hasTasks = projectRun.tasks(myProject).any { task -> task.contains(argument) }
            if (hasTasks) projectRun else null
        }

        if (task.isNotEmpty()) {
            task.first().run(myProject, argument)
            return "Task run successfully: $argument"
        }

        val virtualFile = myProject.lookupFile(argument.trim()) ?: return "$SHIRE_ERROR: File not found: $argument"
        try {
            val psiFile: PsiFile =
                PsiManager.getInstance(myProject).findFile(virtualFile)
                    ?: return "$SHIRE_ERROR: [RunShireCommand] File not found: $argument"

            val testService =
                AutoTesting.context(psiFile) ?: return "$SHIRE_ERROR: [RunShireCommand] No test service found for file: $argument"
            testService.runFile(myProject, virtualFile, null)

            return "Tests run successfully for file: $argument"
        } catch (e: Exception) {
            return "$SHIRE_ERROR: ${e.message}"
        }
    }
}
