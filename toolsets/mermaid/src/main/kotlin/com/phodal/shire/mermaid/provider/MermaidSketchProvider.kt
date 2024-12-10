package com.phodal.shire.mermaid.provider

import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.TextEditorWithPreview
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirecore.provider.sketch.ExtensionLangSketch
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider
import javax.swing.JComponent
import javax.swing.JPanel

class MermaidSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean {
        return lang == "mermaid" || lang == "mmd"
    }

    override fun createSketch(project: Project, content: String): ExtensionLangSketch {
        val file = LightVirtualFile("mermaid.mermaid", content)
        return MermaidSketch(project, file)
    }
}

class MermaidSketch(private val project: Project, private val virtualFile: VirtualFile) : ExtensionLangSketch {
    private var mainPanel: JPanel

    init {
        val editor = getEditorProvider().createEditor(project, virtualFile) as TextEditorWithPreview
        val previewEditor = editor.previewEditor
        mainPanel = panel {
            row {
                cell(editor.editor.component).align(Align.FILL)
            }
            row {
                cell(previewEditor.component).align(Align.FILL)
            }
        }
    }

    private fun getEditorProvider(): FileEditorProvider =
        FileEditorProvider.EP_FILE_EDITOR_PROVIDER.extensionList.filter {
            it.javaClass.simpleName == "MermaidEditorWithPreviewProvider"
        }.firstOrNull() ?: TextEditorProvider.getInstance()

    override fun getExtensionName(): String {
        return "mermaid"
    }

    override fun getViewText(): String {
        return virtualFile.readText()
    }

    override fun updateViewText(text: String) {
    }

    override fun getComponent(): JComponent {
        return mainPanel
    }

    override fun updateLanguage(language: Language?) {
    }

    override fun dispose() {
    }
}
