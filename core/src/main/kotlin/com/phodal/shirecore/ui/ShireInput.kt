package com.phodal.shirecore.ui

import com.intellij.ide.scratch.ScratchFileService
import com.intellij.ide.scratch.ScratchRootType
import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.SHIRE_CHAT_BOX_FILE
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.provider.shire.FileRunService
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class ShireInput(val project: Project) : JPanel(BorderLayout()), Disposable {
    init {
        val inputSection = ShireInputSection(project, this)
        inputSection.addListener(object : ShireInputListener {
            override fun onStop(component: ShireInputSection) {
                inputSection.showSendButton()
            }

            override fun onSubmit(component: ShireInputSection, trigger: ShireInputTrigger) {
                val prompt = component.text
                component.text = ""

                if (prompt.isEmpty() || prompt.isBlank()) {
                    component.showTooltip(ShireCoreBundle.message("chat.input.empty.tips"))
                    return
                }

                val createScratchFile = createShireFile(prompt)

                FileRunService.provider(project, createScratchFile!!)
                    ?.runFile(project, createScratchFile, null)
            }
        })
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.add(inputSection, BorderLayout.CENTER)
    }

    private fun createShireFile(prompt: String): VirtualFile? {
        val header = """
            |---
            |name: "shire-temp"
            |description: "Shire Temp File"
            |interaction: RightPanel
            |---
            |
        """.trimMargin()

        val content = header + prompt

        return ScratchRootType.getInstance().createScratchFile(
            project,
            SHIRE_CHAT_BOX_FILE,
            Language.findLanguageByID("Shire"),
            content,
            ScratchFileService.Option.create_if_missing
        )
    }

    override fun dispose() {
        // do nothing
    }
}