package com.phodal.shirecore.ui.viewer

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.Cell
import javax.swing.JComponent

interface LangSketch: Disposable {
    fun getViewText(): String
    fun updateViewText(text: String)
    fun getComponent(): JComponent
    fun updateLanguage(language: Language?)
}

fun <T : JComponent> Cell<T>.fullWidth(): Cell<T> {
    return this.align(AlignX.FILL)
}

fun <T : JComponent> Cell<T>.fullHeight(): Cell<T> {
    return this.align(AlignY.FILL)
}
