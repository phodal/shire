package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.openapi.project.Project
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.run.ShireConfiguration

abstract class ShireRunner(
    open val configuration: ShireConfiguration,
    open val processHandler: ProcessHandler,
    open val console: ConsoleViewWrapperBase,
    open val myProject: Project,
    open val hole: HobbitHole?,
    open val prompt: String,
) {
    abstract fun execute(postFunction: (response: String) -> Unit)
    fun prepareTask() {

    }

    fun handleResult() {
        when (hole?.interaction) {
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