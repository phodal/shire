package com.phodal.shire.inline

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.phodal.shirecore.provider.ide.InlineChatProvider
import io.ktor.util.collections.*

data class GutterIconData(
    val line: Int,
    val highlighter: RangeHighlighter,
)

class ShireInlineChatProvider: InlineChatProvider {
    override fun addListener(project: Project) {
        EditorGutterHandler.getInstance(project).listen()
    }

    override fun removeListener(project: Project) {
        EditorGutterHandler.getInstance(project).dispose()
    }
}

@Service(Service.Level.PROJECT)
class EditorGutterHandler(private val project: Project) : Disposable {
    val gutterIcons: ConcurrentMap<Editor, GutterIconData?> = ConcurrentMap()

    private var disposable: Disposable? = null

    fun listen() {
        addEditorFactoryListener()
    }

    fun addEditorFactoryListener() {
        if (disposable != null) {
            return
        }
        disposable = Disposer.newDisposable().apply {
            EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
                override fun editorCreated(event: EditorFactoryEvent) {
                    onEditorCreated(event.editor, this@apply)
                }
            }, this)

            FileEditorManager.getInstance(project).allEditors.mapNotNull { it as? TextEditor }.forEach {
                updateGutterIconWithSelection(it.editor)
                onEditorCreated(it.editor, this@apply)
            }
        }
    }

    fun onEditorCreated(editor: Editor, disposable: Disposable) {
        editor.selectionModel.addSelectionListener(object : SelectionListener {
            override fun selectionChanged(e: SelectionEvent) {
                if (e.editor.project != project) return
                updateGutterIconWithSelection(editor)
            }
        }, disposable)
    }

    fun updateGutterIconWithSelection(editor: Editor) {
        if (!editor.selectionModel.hasSelection()) {
            gutterIcons[editor]?.let {
                removeGutterIcon(editor, it.highlighter)
            }

            return
        }

        val selectionStart = editor.document.getLineNumber(editor.selectionModel.selectionStart)
        if (selectionStart >= 0 && selectionStart < editor.document.lineCount) {
            val gutterIconInfo = gutterIcons[editor]
            if (gutterIconInfo?.line != selectionStart) {
                addGutterIcon(editor, selectionStart)
            }
        }
    }

    fun addGutterIcon(editor: Editor, line: Int) {
        val iconData: GutterIconData? = gutterIcons[editor]
        if (iconData != null) {
            removeGutterIcon(editor, iconData.highlighter)
        }

        FileDocumentManager.getInstance().getFile(editor.document) ?: return

        val highlighter = editor.markupModel.addLineHighlighter(null, line, 0)
        highlighter.gutterIconRenderer = ShireGutterIconRenderer(line, onClick = {
            ShireInlineChatService.getInstance().showInlineChat(editor)
        })

        gutterIcons[editor] = GutterIconData(line, highlighter)
    }

    fun removeGutterIcon(editor: Editor, highlighter: RangeHighlighter? = null) {
        if (highlighter != null) editor.markupModel.removeHighlighter(highlighter)
        gutterIcons.remove(editor)
    }

    override fun dispose() {
        gutterIcons.forEach {
            removeGutterIcon(it.key, it.value?.highlighter)
        }
        disposable?.let { Disposer.dispose(it) }
        disposable = null
    }

    companion object {

        fun getInstance(project: Project): EditorGutterHandler {
            return project.getService(EditorGutterHandler::class.java)
        }
    }
}

