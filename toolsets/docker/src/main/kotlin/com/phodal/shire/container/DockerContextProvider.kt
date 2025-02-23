package com.phodal.shire.container

import com.intellij.docker.DockerFileSearch
import com.intellij.docker.dockerFile.parser.psi.DockerFileFromCommand
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.PsiFileImpl
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext

class DockerContextProvider : LanguageToolchainProvider {
    override fun isApplicable(
        project: Project,
        context: ToolchainPrepareContext,
    ): Boolean = DockerFileSearch.getInstance().getDockerFiles(project).isNotEmpty()

    override suspend fun collect(
        project: Project,
        context: ToolchainPrepareContext,
    ): List<ToolchainContextItem> {
        val dockerFiles = DockerFileSearch.getInstance().getDockerFiles(project).mapNotNull {
            runReadAction { PsiManager.getInstance(project).findFile(it) }
        }

        if (dockerFiles.isEmpty()) return emptyList()

        var context = "This project use Docker."

        val virtualFile = dockerFiles.firstOrNull()?.virtualFile
            ?: return listOf(ToolchainContextItem(DockerContextProvider::class, context))

        context = "This project use Docker, path: ${virtualFile.path}"

        var additionalCtx = ""
        val fromCommands = dockerFiles.map {
            (it as PsiFileImpl).findChildrenByClass(DockerFileFromCommand::class.java).toList()
        }.flatten()

        if (fromCommands.isEmpty()) return listOf(ToolchainContextItem(DockerContextProvider::class, context))
        additionalCtx = fromCommands.joinToString("\n") {
            runReadAction { it.text }
        }

        val text = "This project use Docker to run in server. Here is related info:\n$additionalCtx"
        return listOf(ToolchainContextItem(DockerContextProvider::class, text))
    }
}

fun VirtualFile.readText(): String {
    return VfsUtilCore.loadText(this)
}
