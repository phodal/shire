package com.phodal.shirelang.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirelang.ShireBundle
import javax.swing.JComponent

class ShireSettingsEditor(val project: Project) : SettingsEditor<ShireConfiguration>() {
    private val myScriptSelector: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    init {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        val message = ShireBundle.message("devin.label.choose.file")

        myScriptSelector.addBrowseFolderListener(message, "", project, descriptor)
    }

    override fun createEditor(): JComponent = panel {
        row {
            cell(myScriptSelector).align(AlignX.FILL)
        }
    }

    override fun resetEditorFrom(configuration: ShireConfiguration) {
        myScriptSelector.text = configuration.getScriptPath()
    }

    override fun applyEditorTo(configuration: ShireConfiguration) {
        configuration.setScriptPath(myScriptSelector.text)
    }
}