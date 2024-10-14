package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.lookupFile
import com.phodal.shirecore.provider.TestingService
import com.phodal.shirecore.provider.shire.ProjectRunService
import com.phodal.shirelang.compiler.parser.SHIRE_ERROR

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

        val virtualFile = myProject.lookupFile(argument.trim()) ?: return "$SHIRE_ERROR: [RunShireCommand] File not found: $argument"
        try {
            val psiFile: PsiFile =
                PsiManager.getInstance(myProject).findFile(virtualFile)
                    ?: return "$SHIRE_ERROR: [RunShireCommand] File not found: $argument"

            val testService =
                TestingService.context(psiFile)
                    ?: return "$SHIRE_ERROR: [RunShireCommand] No test service found for file: $argument"
            testService.runFile(myProject, virtualFile, null)

            return "Tests run successfully for file: $argument"
        } catch (e: Exception) {
            return "$SHIRE_ERROR: ${e.message}"
        }
    }
}
