package com.phodal.shirecore.ui.input

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.provider.shire.FileCreateService
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

                val virtualFile = getVirtualFile(prompt)
                this@ShireInput.scratchFile = virtualFile

                FileRunService.provider(project, virtualFile!!)
                    ?.runFile(project, virtualFile, null)
            }
        })
        this.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        this.add(inputSection, BorderLayout.CENTER)
    }

    private fun getVirtualFile(prompt: String): VirtualFile? {
        val findLanguageByID = Language.findLanguageByID("Shire")
        val provide = FileCreateService.provide(findLanguageByID!!)
        return provide!!.createFile(prompt, project)
    }

    override fun dispose() {
        scratchFile?.delete(this)
    }
}