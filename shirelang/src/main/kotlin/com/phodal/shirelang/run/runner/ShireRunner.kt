package com.phodal.shirelang.run.runner

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.run.ShireConfiguration

data class ShireRunnerContext(
    val configuration: ShireConfiguration,
    val processHandler: ProcessHandler,
    val console: ConsoleView,
    val myProject: Project,
    val hole: HobbitHole?,
    val prompt: String,
    val editor: Editor?,
)

abstract class ShireRunner(open val context: ShireRunnerContext) {
    abstract fun execute(postFunction: (response: String) -> Unit)
    fun prepareTask() {

    }

    fun handleResult() {
        when (context.hole?.interaction) {
            InteractionType.AppendCursor -> TODO()
            InteractionType.AppendCursorStream -> TODO()
            InteractionType.OutputFile -> TODO()
            InteractionType.ReplaceSelection -> TODO()
            InteractionType.ReplaceCurrentFile -> TODO()
            InteractionType.InsertBeforeSelection -> {
                TODO()
            }

            null -> TODO()
        }
    }
}