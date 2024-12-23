package com.phodal.shirecore.ui.input

import com.intellij.openapi.editor.ex.EditorEx
import java.util.*

interface ShireInputListener : EventListener {
    fun editorAdded(editor: EditorEx) {}
    fun onSubmit(component: ShireInputSection, trigger: ShireInputTrigger) {}
    fun onStop(component: ShireInputSection) {}
}