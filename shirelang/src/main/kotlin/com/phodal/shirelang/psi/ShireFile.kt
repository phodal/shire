package com.phodal.shirelang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.*
import com.phodal.shirelang.ShireFileType
import com.phodal.shirelang.ShireLanguage
import java.util.*

class ShireFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ShireLanguage.INSTANCE) {
    override fun getFileType(): FileType = ShireFileType.INSTANCE

    override fun getOriginalFile(): ShireFile = super.getOriginalFile() as ShireFile

    override fun toString(): String = "ShireFile"

    override fun getStub(): ShireFileStub? = super.getStub() as ShireFileStub ?

    companion object {
        /**
         * Create a tempShireFile from a string.
         */
        fun fromString(project: Project, text: String): ShireFile {
            val filename =
                ShireLanguage.displayName + "-${UUID.randomUUID()}." + ShireFileType.INSTANCE.defaultExtension
            val shireFile = runReadAction {
                PsiFileFactory.getInstance(project).createFileFromText(filename, ShireLanguage, text) as ShireFile
            }

            return shireFile
        }

        fun lookup(project: Project, path: String) = VirtualFileManager.getInstance()
            .findFileByUrl("file://$path")
            ?.let {
                PsiManager.getInstance(project).findFile(it)
            } as? ShireFile
    }
}

