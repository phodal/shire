package com.phodal.shirecore.ui.input

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.lang.Language
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.SHIRE_CHAT_BOX_FILE

@Service(Service.Level.PROJECT)
class ChatBoxShireFileService {
    fun createShireFile(prompt: String, project: Project): VirtualFile? {
        val header = """
                |---
                |name: "shire-temp"
                |description: "Shire Temp File"
                |interaction: RightPanel
                |---
                |
            """.trimMargin()

        val content = header + prompt

        val virtualFile = ScratchRootType.getInstance().createScratchFile(
            project,
            SHIRE_CHAT_BOX_FILE,
            Language.findLanguageByID("Shire"),
            content,
            ScratchFileService.Option.create_if_missing
        )

        return virtualFile
    }
}