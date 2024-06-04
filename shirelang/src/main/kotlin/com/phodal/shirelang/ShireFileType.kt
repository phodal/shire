package com.phodal.shirelang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

object ShireFileType : LanguageFileType(ShireLanguage) {
    override fun getName(): String = "ShireFile"

    override fun getIcon(): Icon = ShireIcons.DEFAULT

    override fun getDefaultExtension(): String = "shire"

    override fun getCharset(file: VirtualFile, content: ByteArray): String = "UTF-8"

    override fun getDescription(): String = "Shire file"
}