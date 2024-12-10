package com.phodal.shirecore.ui.viewer

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.sketch.ExtensionLangSketch
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider

class DiffLangSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean {
        return lang == "diff"
    }

    override fun createSketch(project: Project, content: String): ExtensionLangSketch {
        return DiffLangSketch(project, content)
    }
}
