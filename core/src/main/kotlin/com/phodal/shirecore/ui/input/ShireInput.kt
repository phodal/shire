package com.phodal.shirecore.ui.input

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
    private var scratchFile: VirtualFile? = null

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

                val virtualFile = Companion.createShireFile(prompt, project)
                this@ShireInput.scratchFile = virtualFile

                FileRunService.provider(project, virtualFile!!)
                    ?.runFile(project, virtualFile, null)
            }
        })
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.add(inputSection, BorderLayout.CENTER)
    }

    override fun dispose() {
        scratchFile?.delete(this)
    }

    companion object {
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
}