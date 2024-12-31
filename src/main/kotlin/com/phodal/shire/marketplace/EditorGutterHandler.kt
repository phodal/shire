package com.phodal.shire.marketplace

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.phodal.shire.ShireIdeaIcons
import com.phodal.shirecore.ShirelangNotifications
import io.ktor.util.collections.*
import javax.swing.Icon

data class GutterIconData(
    val line: Int,
    val highlighter: RangeHighlighter,
)

class EditorGutterHandler {
    val gutterIcons: ConcurrentMap<Editor, GutterIconData?> = ConcurrentMap()

    fun create() {
        addEditorFactoryListener()
    }

    fun addEditorFactoryListener() {
        EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
            override fun editorCreated(event: EditorFactoryEvent) {
                INSTANCE.onEditorCreated(event.editor)
            }
        }, ApplicationManager.getApplication())
    }

    fun onEditorCreated(editor: Editor) {
        editor.selectionModel.addSelectionListener(object : SelectionListener {
            override fun selectionChanged(e: SelectionEvent) {
                if (!editor.hasSelection()) {
                    gutterIcons[editor]?.let {
                        INSTANCE.removeGutterIcon(editor, it.highlighter)
                    }
                }

                val selectionStart = editor.document.getLineNumber(e.newRange.startOffset)
                if (selectionStart >= 0 && selectionStart < editor.document.lineCount) {
                    val gutterIconInfo = gutterIcons[editor]
                    if (gutterIconInfo?.line != selectionStart) {
                        INSTANCE.addGutterIcon(editor, selectionStart)
                    }
                }
            }
        })
    }

    fun addGutterIcon(editor: Editor, line: Int) {
        val it: GutterIconData? = gutterIcons.get(editor)
        if (it != null) {
            INSTANCE.removeGutterIcon(editor, it.highlighter)
        }


        FileDocumentManager.getInstance().getFile(editor.document) ?: return

        val highlighter = editor.markupModel.addLineHighlighter(null, line, 0)
        highlighter.gutterIconRenderer = ShireGutterIconRenderer(line, onClick = {
            ShirelangNotifications.info(editor.project!!, "Line $line clicked")
        })

        gutterIcons[editor] = GutterIconData(line, highlighter)
    }

    fun removeGutterIcon(editor: Editor, highlighter: RangeHighlighter) {
        editor.markupModel.removeHighlighter(highlighter)
    }

    companion object {
        val INSTANCE = EditorGutterHandler()
    }
}

class ShireGutterIconRenderer(
    val line: Int, val onClick: () -> Unit,
) : GutterIconRenderer() {
    override fun getClickAction(): AnAction {
        return object : AnAction() {
            override fun actionPerformed(e: AnActionEvent) {
                onClick()
            }
        }
    }

    override fun getIcon(): Icon = ShireIdeaIcons.Default
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShireGutterIconRenderer

        if (line != other.line) return false
        if (onClick != other.onClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + onClick.hashCode()
        return result
    }

}

fun Editor.addSelectionListener(listener: SelectionListener) {
    selectionModel.addSelectionListener(listener)
}

fun Editor.hasSelection(): Boolean {
    return selectionModel.hasSelection()
}