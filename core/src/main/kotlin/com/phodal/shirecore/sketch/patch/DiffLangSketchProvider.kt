package com.phodal.shirecore.sketch.patch

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.sketch.ExtensionLangSketch
import com.phodal.shirecore.provider.sketch.LanguageSketchProvider

class DiffLangSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean {
        return lang == "diff" || lang == "patch"
    }

    override fun createSketch(project: Project, content: String): ExtensionLangSketch {
        return DiffLangSketch(project, content)
    }
}
