package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.charset.StandardCharsets

object ShireShellRunner {
    private const val DEFAULT_TIMEOUT: Int = 30000

    fun runShellCommand(virtualFile: VirtualFile, myProject: Project, processVariables: Map<String, String>): String {
        val workingDirectory = virtualFile.parent.path

        val commandLine: GeneralCommandLine = GeneralCommandLine()
            .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
            .withWorkDirectory(workingDirectory)
            .withCharset(StandardCharsets.UTF_8)
            .withExePath("sh")
            .withParameters(virtualFile.path)

        val processOutput = CapturingProcessHandler(commandLine).runProcess(DEFAULT_TIMEOUT)
        val exitCode = processOutput.exitCode
        if (exitCode != 0) {
            throw RuntimeException("Cannot execute ${commandLine}: exit code $exitCode, error output: ${processOutput.stderr}")
        }

        return processOutput.stdout
    }
}