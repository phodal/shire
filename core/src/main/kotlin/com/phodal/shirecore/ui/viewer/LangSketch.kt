package com.phodal.shirecore.ui.viewer

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import javax.swing.JComponent

interface LangSketch: Disposable {
    fun getViewText(): String
    fun updateViewText(text: String)
    fun getComponent(): JComponent
    fun updateLanguage(language: Language?)
    fun doneUpdateText(text: String) { }
}
