package com.phodal.shirecore.provider.sketch

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.sketch.LangSketch


interface ExtensionLangSketch: LangSketch {
    fun getExtensionName(): String
}

interface LanguageSketchProvider {
    fun isSupported(lang: String): Boolean

    fun createSketch(project: Project, content: String): ExtensionLangSketch

    companion object {
        private val EP_NAME: ExtensionPointName<LanguageSketchProvider> =
            ExtensionPointName("com.phodal.shireLangSketchProvider")

        fun provide(language: String): LanguageSketchProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isSupported(language)
            }
        }
    }
}