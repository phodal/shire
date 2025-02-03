package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diff.impl.patch.FilePatch
import com.intellij.openapi.diff.impl.patch.PatchReader
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.patch.AbstractFilePatchInProgress
import com.intellij.openapi.vcs.changes.patch.ApplyPatchDefaultExecutor
import com.intellij.openapi.vcs.changes.patch.MatchPatchPaths
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.MultiMap
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class PatchShireCommand(val myProject: Project, private val prop: String, private val codeContent: String) :
    ShireCommand {
    override val commandName = BuiltinCommand.PATCH

    override suspend fun doExecute(): String {
        FileDocumentManager.getInstance().saveAllDocuments()

        val shelfExecutor = ApplyPatchDefaultExecutor(myProject)

        val myReader = PatchReader(codeContent)
        myReader.parseAllPatches()

        val filePatches: MutableList<FilePatch> = myReader.allPatches

        ApplicationManager.getApplication().invokeLater {
            val matchedPatches =
                MatchPatchPaths(myProject).execute(filePatches, true)

            val patchGroups = MultiMap<VirtualFile, AbstractFilePatchInProgress<*>>()
            for (patchInProgress in matchedPatches) {
                patchGroups.putValue(patchInProgress.base, patchInProgress)
            }

            val additionalInfo = myReader.getAdditionalInfo(ApplyPatchDefaultExecutor.pathsFromGroups(patchGroups))
            shelfExecutor.apply(filePatches, patchGroups, null, prop, additionalInfo)
        }

        return "Patch in Progress..."
    }

}