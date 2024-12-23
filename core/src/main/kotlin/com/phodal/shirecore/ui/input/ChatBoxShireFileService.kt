package com.phodal.shirecore.ui.input

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.SHIRE_CHAT_BOX_FILE
import org.intellij.lang.annotations.Language

@Service(Service.Level.PROJECT)
class ChatBoxShireFileService(val project: Project) {
    private var baseContent: String = ""

    fun createShireFile(prompt: String, project: Project): VirtualFile? {
        if (baseContent.isNotEmpty()) {
            return createInputWithBase(prompt, project)
        }

        return createInputOnly(prompt, project)
    }

    private fun createInputOnly(
        prompt: String,
        project: Project,
    ): VirtualFile? {
        @Language("Shire")
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
            com.intellij.lang.Language.findLanguageByID("Shire"),
            content,
            ScratchFileService.Option.create_if_missing
        )

        return virtualFile
    }

    private fun createInputWithBase(
        prompt: String,
        project: Project,
    ): VirtualFile? {
        val content = baseContent.replace("\$chatPrompt", prompt)

        val virtualFile = ScratchRootType.getInstance().createScratchFile(
            project,
            SHIRE_CHAT_BOX_FILE,
            com.intellij.lang.Language.findLanguageByID("Shire"),
            content,
            ScratchFileService.Option.create_if_missing
        )

        return virtualFile
    }
}