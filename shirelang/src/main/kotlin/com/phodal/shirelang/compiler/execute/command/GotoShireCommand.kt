package com.phodal.shirelang.compiler.execute.command

import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.lookupFile
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirelang.compiler.ast.LineInfo
import com.phodal.shirelang.compiler.parser.SHIRE_ERROR
import com.phodal.shirelang.psi.ShireUsed

/**
 * The goto command will open the file and move the cursor to the specified line.
 * For example:
 *
 * ```shire
 * /goto:src/main/shire/com/phodal/shirelang/compiler/execute/command/GotoShireCommand.kt#L10C8
 * ```
 *
 * means to open the file `src/main/shire/com/phodal/shirelang/compiler/execute/command/GotoShireCommand.kt`
 * and move the cursor to line 10, column 8.
 */
class GotoShireCommand(val myProject: Project, private val argument: String, val used: ShireUsed) : ShireCommand {
    override suspend fun doExecute(): String {
        if (argument.contains(".")) {
            return gotoSymbol()
        }

        val range: LineInfo? = LineInfo.fromString(used.text)
        return gotoFile(range)
    }

    private fun gotoSymbol(): String {
        val symbols = ShireSymbolProvider.all().map {
            it.resolveSymbol(myProject, argument)
        }.flatten()

        if (symbols.isEmpty()) {
            return "$SHIRE_ERROR: Symbol not found: $argument"
        }

        val results: List<String> = symbols.map { symbol ->
            val hyperlinkInfo = OpenFileHyperlinkInfo(myProject, symbol.containingFile.virtualFile, symbol.textOffset)
            hyperlinkInfo.navigate(myProject)
            symbol.containingFile.virtualFile.path
        }

        return results.joinToString("\n")
    }

    private fun gotoFile(range: LineInfo?): String {
        val virtualFile = runReadAction { myProject.lookupFile(argument) }
        if (virtualFile == null) {
            return "$SHIRE_ERROR: File not found: $argument"
        }

        val editor =
            FileEditorManager.getInstance(myProject).selectedTextEditor ?: return "$SHIRE_ERROR: Editor not found"
        val line = range?.startLine ?: 0
        val column = range?.startColumn ?: 0

        runReadAction {
            val offset = editor.document.let {
                val lineStartOffset = it.getLineStartOffset(line.coerceIn(0, it.lineCount - 1))
                (lineStartOffset + column).coerceAtMost(it.textLength)
            }

            editor.caretModel.moveToOffset(offset)
            editor.scrollingModel.scrollToCaret(ScrollType.CENTER)
        }

        return "Navigated to $argument at line $line, column $column"
    }
}
