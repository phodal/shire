package com.phodal.shirecore.index

import com.intellij.json.JsonFileType
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter

class ShireEnvironmentInputFilter : DefaultFileTypeSpecificInputFilter(*arrayOf<FileType>(JsonFileType.INSTANCE)) {
    override fun acceptInput(file: VirtualFile): Boolean {
        return super.acceptInput(file) && isShireEnvFile(file)
    }

    private fun isShireEnvFile(file: VirtualFile?): Boolean {
        if (file != null) {
            val fileName = file.name
            return fileName.endsWith(".shireEnv.json")
        }

        return false
    }
}