package com.phodal.shirelang.run.runner

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.run.ShireConfiguration

data class ShireLlmRunnerContext(
    val configuration: ShireConfiguration,
    val processHandler: ProcessHandler,
    val console: ConsoleView,
    val myProject: Project,
    val hole: HobbitHole?,
    val prompt: String,
    val editor: Editor?,
)

abstract class ShireLlmRunner(open val context: ShireLlmRunnerContext) {
    abstract fun execute(postFunction: PostFunction)
    fun prepareTask() {

    }
}
