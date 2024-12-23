package com.phodal.shirelang.provider

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.SHIRE_CHAT_BOX_FILE
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.shire.FileCreateService
import com.phodal.shirelang.actions.base.DynamicShireActionService
import org.intellij.lang.annotations.Language

class ChatBoxShireFileCreateService : FileCreateService {
    override fun createFile(prompt: String, project: Project): VirtualFile? {
        val actions = DynamicShireActionService.getInstance(project).getActions(ShireActionLocation.CHAT_BOX)
        var baseContent = ""
        if (actions.isNotEmpty()) {
            baseContent = actions.first().shireFile.text ?: ""
        }

        if (baseContent.isNotEmpty()) {
            return createInputWithBase(prompt, project, baseContent)
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
        baseContent: String,
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