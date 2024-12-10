package com.phodal.shire.mermaid.provider

import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider
import com.phodal.shirecore.ui.viewer.LangSketch
import javax.swing.JComponent
import com.intellij.mermaid.preview.MermaidDiagramPreviewComponent
import javax.swing.JPanel

class MermaidSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean {
        return lang == "mermaid"
    }

    override fun createSketch(project: Project, content: String): LangSketch {
        TODO("Not yet implemented")
    }
}

class MermaidSketch(private val project: Project, private val content: String) : LangSketch {
    private var mainPanel: JPanel

    init {
        val component = MermaidDiagramPreviewComponent(project)
        mainPanel = panel {
            row {
                component.also {
                    it.isVisible = true
                    it.setSize(800, 600)
                }
            }
        }
    }

    override fun getViewText(): String {
        return content
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
