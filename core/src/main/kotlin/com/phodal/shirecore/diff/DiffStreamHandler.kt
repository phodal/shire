package com.phodal.shirecore.diff

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.command.undo.UndoManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.diff.model.DiffLine
import com.phodal.shirecore.diff.model.streamDiff
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.utils.markdown.CodeFence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.min


enum class DiffLineType {
    SAME, NEW, OLD
}

class DiffStreamHandler(
    private val project: Project,
    private val editor: Editor,
    private val startLine: Int,
    private val endLine: Int,
    private val onClose: () -> Unit,
    private val onFinish: () -> Unit,
) {
    private data class CurLineState(
        var index: Int, var highlighter: RangeHighlighter? = null, var diffBlock: VerticalDiffBlock? = null,
    )

    private var curLine = CurLineState(startLine)

    private var isRunning: Boolean = false
    private var hasAcceptedOrRejectedBlock: Boolean = false

    private val unfinishedHighlighters: MutableList<RangeHighlighter> = mutableListOf()
    private val diffBlocks: MutableList<VerticalDiffBlock> = mutableListOf()

    private val curLineKey = createTextAttributesKey("CONTINUE_DIFF_CURRENT_LINE", 0x40888888, editor)
    private val unfinishedKey = createTextAttributesKey("CONTINUE_DIFF_UNFINISHED_LINE", 0x20888888, editor)

    init {
        initUnfinishedRangeHighlights()
    }

    fun acceptAll() {
        editor.markupModel.removeAllHighlighters()
        resetState()
    }

    fun rejectAll() {
        // The ideal action here is to undo all changes we made to return the user's edit buffer to the state prior
        // to our changes. However, if the user has accepted or rejected one or more diff blocks, there isn't a simple
        // way to undo our changes without also undoing the diff that the user accepted or rejected.
        if (hasAcceptedOrRejectedBlock) {
            diffBlocks.forEach { it.handleReject() }
        } else {
            undoChanges()
        }

        resetState()
    }

    fun streamDiffLinesToEditor(originContent: String, prompt: String) {
        val lines = originContent.lines()
        /// commit document
        val document = editor.document
        PsiDocumentManager.getInstance(project).commitDocument(document)

        isRunning = true
        val flow: Flow<String> = LlmProvider.provider(project)!!.stream(prompt, "", false)
        var lastLineNo = 0
        ShireCoroutineScope.scope(project).launch {
            val suggestion = StringBuilder()
            flow.cancellable().collect { char ->
                suggestion.append(char)
                val code = CodeFence.parse(suggestion.toString())
                if (PlainTextLanguage.INSTANCE != code.ideaLanguage && code.ideaLanguage.displayName != "Markdown" && code.text.isNotEmpty()) {
                    var value: List<String> = code.text.lines()

                    // we don't need the last line, maybe not finished
                    value = value.dropLast(1)

                    if (value.isEmpty()) {
                        return@collect
                    }

                    val newLines = if (lastLineNo + value.size < lines.size) {
                        lines.subList(lastLineNo, lastLineNo + value.size)
                    } else {
                        listOf()
                    }

                    if (newLines.isEmpty()) {
                        return@collect
                    }

                    val flowValue: Flow<String> = flowOf(*newLines.toTypedArray())
                    lastLineNo = value.size

                    // pick until oldLines.size from originContent
                    val oldLinesContent = if (lastLineNo + newLines.size < lines.size) {
                        lines.subList(lastLineNo, lastLineNo + newLines.size)
                    } else {
                        listOf()
                    }

                    streamDiff(oldLinesContent, flowValue).collect {
                        ApplicationManager.getApplication().invokeLater {
                            WriteCommandAction.runWriteCommandAction(project) {
                                when (it) {
                                    is DiffLine.New -> {
                                        handleNewLine(it.line)
                                    }

                                    is DiffLine.Old -> {
                                        handleOldLine()
                                    }

                                    is DiffLine.Same -> {
                                        handleSameLine()
                                    }
                                }
                            }
                        }
                    }
                }
            }

            handleFinishedResponse()
        }
    }

    private fun initUnfinishedRangeHighlights() {
        for (i in startLine..endLine) {
            val highlighter = editor.markupModel.addLineHighlighter(
                unfinishedKey, min(
                    i, editor.document.lineCount - 1
                ), HighlighterLayer.LAST
            )
            unfinishedHighlighters.add(highlighter)
        }
    }

    private fun handleDiffLine(type: DiffLineType, text: String) {
        try {
            when (type) {
                DiffLineType.SAME -> handleSameLine()
                DiffLineType.NEW -> handleNewLine(text)
                DiffLineType.OLD -> handleOldLine()
            }

            updateProgressHighlighters(type)
        } catch (e: Exception) {
            println(
                "Error handling diff line - " +
                        "Line index: ${curLine.index}, " +
                        "Line type: $type, " +
                        "Line text: $text, " +
                        "Error message: ${e.message}"
            )
        }
    }

    private fun handleDiffBlockAcceptOrReject(diffBlock: VerticalDiffBlock, didAccept: Boolean) {
        hasAcceptedOrRejectedBlock = true

        diffBlocks.remove(diffBlock)

        if (!didAccept) {
            updatePositionsOnReject(diffBlock.startLine, diffBlock.addedLines.size, diffBlock.deletedLines.size)
        }

        if (diffBlocks.isEmpty()) {
            onClose()
        }
    }


    private fun createDiffBlock(): VerticalDiffBlock {
        val diffBlock = VerticalDiffBlock(
            editor, project, curLine.index, ::handleDiffBlockAcceptOrReject
        )

        diffBlocks.add(diffBlock)

        return diffBlock
    }

    private fun handleSameLine() {
        if (curLine.diffBlock != null) {
            curLine.diffBlock!!.onLastDiffLine()
        }

        curLine.diffBlock = null

        curLine.index++
    }

    private fun handleNewLine(text: String) {
        if (curLine.diffBlock == null) {
            curLine.diffBlock = createDiffBlock()
        }

        curLine.diffBlock!!.addNewLine(text, curLine.index)

        curLine.index++
    }

    private fun handleOldLine() {
        if (curLine.diffBlock == null) {
            curLine.diffBlock = createDiffBlock()
        }

        curLine.diffBlock!!.deleteLineAt(curLine.index)
    }

    private fun updateProgressHighlighters(type: DiffLineType) {
        // Update the highlighter to show the current line
        curLine.highlighter?.let { editor.markupModel.removeHighlighter(it) }
        curLine.highlighter = editor.markupModel.addLineHighlighter(
            curLineKey, min(curLine.index, editor.document.lineCount - 1), HighlighterLayer.LAST
        )

        // Remove the unfinished lines highlighter
        if (type != DiffLineType.OLD && unfinishedHighlighters.isNotEmpty()) {
            editor.markupModel.removeHighlighter(unfinishedHighlighters.removeAt(0))
        }
    }


    private fun updatePositionsOnReject(startLine: Int, numAdditions: Int, numDeletions: Int) {
        val offset = -numAdditions + numDeletions

        diffBlocks.forEach { block ->
            if (block.startLine > startLine) {
                block.updatePosition(block.startLine + offset)
            }
        }
    }

    private fun resetState() {
        // Clear the editor of highlighting/inlays
        editor.markupModel.removeAllHighlighters()
        diffBlocks.forEach { it.clearEditorUI() }

        // Clear state vars
        diffBlocks.clear()
        curLine = CurLineState(startLine)
        isRunning = false

        // Close the Edit input
        onClose()
    }


    private fun undoChanges() {
        WriteCommandAction.runWriteCommandAction(project) {
            val undoManager = UndoManager.getInstance(project)
            val virtualFile = getVirtualFile() ?: return@runWriteCommandAction
            val fileEditor = FileEditorManager.getInstance(project).getSelectedEditor(virtualFile) as TextEditor

            if (undoManager.isUndoAvailable(fileEditor)) {
                val numChanges = diffBlocks.sumOf { it.deletedLines.size + it.addedLines.size }

                repeat(numChanges) {
                    undoManager.undo(fileEditor)
                }
            }
        }
    }

    private fun getVirtualFile(): VirtualFile? {
        return FileDocumentManager.getInstance().getFile(editor.document) ?: return null
    }

    private fun handleFinishedResponse() {
        ApplicationManager.getApplication().invokeLater {
            // Since we only call onLastDiffLine() when we reach a "same" line, we need to handle the case where
            // the last line in the diff stream is in the middle of a diff block.
            curLine.diffBlock?.onLastDiffLine()

            onFinish()
            cleanupProgressHighlighters()
        }
    }

    private fun cleanupProgressHighlighters() {
        curLine.highlighter?.let { editor.markupModel.removeHighlighter(it) }
        unfinishedHighlighters.forEach { editor.markupModel.removeHighlighter(it) }
    }
}