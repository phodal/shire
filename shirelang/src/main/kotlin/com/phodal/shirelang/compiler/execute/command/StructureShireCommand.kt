package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.codemodel.FileStructureProvider
import org.jetbrains.kotlin.asJava.classes.runReadAction

class StructureShireCommand(val myProject: Project, val prop: String) : ShireCommand {
    private val logger = logger<StructureShireCommand>()
    override suspend fun doExecute(): String? {
        val virtualFile = FileShireCommand.file(myProject, prop)
        if (virtualFile == null) {
            logger.warn("File not found: $prop")
            return null
        }

        val psiFile = runReadAction {
            PsiManager.getInstance(myProject).findFile(virtualFile)
        } ?: return null

        FileStructureProvider.from(psiFile).let {
            return it?.format()
        }
    }
}