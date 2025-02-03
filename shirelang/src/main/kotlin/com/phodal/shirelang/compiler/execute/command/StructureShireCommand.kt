package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.codemodel.FileStructureProvider
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StructureShireCommand(val myProject: Project, val prop: String) : ShireCommand {
    override val commandName = BuiltinCommand.STRUCTURE

    override suspend fun doExecute(): String? {
        val virtualFile = FileShireCommand.file(myProject, prop)
        if (virtualFile == null) {
            logger<StructureShireCommand>().warn("File not found: $prop")
            return null
        }

        val psiFile: PsiFile = withContext(Dispatchers.IO) {
            ApplicationManager.getApplication().executeOnPooledThread<PsiFile?> {
                runReadAction {
                    PsiManager.getInstance(myProject).findFile(virtualFile)
                }
            }.get()
        } ?: return null

        FileStructureProvider.from(psiFile).let {
            return it?.format()
        }
    }
}