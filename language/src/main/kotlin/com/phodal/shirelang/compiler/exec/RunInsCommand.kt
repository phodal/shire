package com.phodal.shirelang.compiler.exec

import com.intellij.openapi.project.Project
import com.phodal.shirelang.compiler.error.SHIRE_ERROR
import com.phodal.shirelang.utils.lookupFile

/**
 * The `RunAutoCommand` class is responsible for executing an auto command on a given project.
 *
 * @property myProject The project to execute the auto command on.
 * @property argument The name of the file to find and run tests for.
 *
 */
class RunInsCommand(val myProject: Project, private val argument: String) : InsCommand {
    override suspend fun execute(): String? {
        val virtualFile = myProject.lookupFile(argument.trim()) ?: return "$SHIRE_ERROR: File not found: $argument"
        try {
//            val psiFile: PsiFile =
//                PsiManager.getInstance(myProject).findFile(virtualFile) ?: return "$SHIRE_ERROR: File not found: $argument"
//            val testService =
//                AutoTestService.context(psiFile) ?: return "$SHIRE_ERROR: No test service found for file: $argument"
//            testService.runFile(myProject, virtualFile, null)
            throw NotImplementedError()
        } catch (e: Exception) {
            return "$SHIRE_ERROR: ${e.message}"
        }
    }
}
