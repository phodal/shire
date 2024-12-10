package com.phodal.shire.plantuml

import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.dsl.builder.panel
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider
import com.phodal.shirecore.ui.viewer.LangSketch
import org.plantuml.idea.preview.editor.PlantUmlPreviewEditor
import org.plantuml.idea.preview.PlantUmlPreviewPanel
import javax.swing.JComponent

class PlantUmlSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean {
        return lang == "plantuml" || lang == "puml" || lang == "uml"
    }

    override fun createSketch(project: Project, content: String): LangSketch {
        val virtualFile = LightVirtualFile("plantuml.puml", content)
        return PlantUmlSketch(project, virtualFile)
    }
}

class PlantUmlSketch(private val project: Project, private val virtualFile: VirtualFile) : LangSketch {
    private var mainPanel: JComponent

    init {
        val editor = PlantUmlPreviewEditor(virtualFile, project)
        val plantUmlPreviewPanel = PlantUmlPreviewPanel(project, editor)

        mainPanel = panel {
            row {
                plantUmlPreviewPanel.also {
                    it.isVisible = true
                    it.setSize(800, 600)
                }
            }
        }
    }

    override fun getViewText(): String {
        return virtualFile.inputStream.bufferedReader().use { it.readText() }
    }

    override fun updateViewText(text: String) {
        virtualFile.setBinaryContent(text.toByteArray())
    }

    override fun getComponent(): JComponent {
        return mainPanel!!
    }

    override fun updateLanguage(language: Language?) {
    }

    override fun dispose() {
    }
}
