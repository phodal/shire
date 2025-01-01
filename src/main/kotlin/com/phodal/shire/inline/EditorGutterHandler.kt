package com.phodal.shire.inline

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import io.ktor.util.collections.*

data class GutterIconData(
    val line: Int,
    val highlighter: RangeHighlighter,
)

class EditorGutterHandler {
    val gutterIcons: ConcurrentMap<Editor, GutterIconData?> = ConcurrentMap()

    fun listen() {
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
                if (!editor.selectionModel.hasSelection()) {
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
        val iconData: GutterIconData? = gutterIcons[editor]
        if (iconData != null) {
            INSTANCE.removeGutterIcon(editor, iconData.highlighter)
        }

        FileDocumentManager.getInstance().getFile(editor.document) ?: return

        val highlighter = editor.markupModel.addLineHighlighter(null, line, 0)
        highlighter.gutterIconRenderer = ShireGutterIconRenderer(line, onClick = {
            ShireInlineChatService.getInstance().showInlineChat(editor)
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

