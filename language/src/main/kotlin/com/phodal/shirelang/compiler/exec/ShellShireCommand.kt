package com.phodal.shirelang.compiler.exec

import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.sh.psi.ShFile
import com.intellij.sh.run.ShRunner
import com.phodal.shirelang.utils.lookupFile

/**
 * A class that implements the `InsCommand` interface to execute a shell command within the IntelliJ IDEA environment.
 *
 * This class is designed to run a shell command specified by a given `prop` string, which is assumed to be the path to a file within the project.
 * The command is executed in a shell runner service provided by IntelliJ IDEA, using the specified file's path and its parent directory as the working directory.
 *
 * @param myProject The current project context.
 * @param argument The path to the file within the project whose content should be executed as a shell command.
 */
class ShellShireCommand(val myProject: Project, private val argument: String) : ShireCommand {
    override suspend fun doExecute(): String {
        val virtualFile = myProject.lookupFile(argument.trim()) ?: return "$SHIRE_ERROR: File not found: $argument"
        val psiFile = PsiManager.getInstance(myProject).findFile(virtualFile) as? ShFile
//        val settings: RunnerAndConfigurationSettings? = ShellRunService().createRunSettings(myProject, virtualFile, psiFile)

//        if (settings != null) {
//            ShellRunService().runFile(myProject, virtualFile, psiFile)
//            return "Running shell file: $argument"
//        }


        val workingDirectory = virtualFile.parent.path
        val shRunner = ApplicationManager.getApplication().getService(ShRunner::class.java)
            ?: return "$SHIRE_ERROR: Shell runner not found"

        if (shRunner.isAvailable(myProject)) {
            shRunner.run(myProject, virtualFile.path, workingDirectory, "RunDevInsShell", true)
        }

        throw NotImplementedError()

        return "Running shell command: $argument"
    }
}
