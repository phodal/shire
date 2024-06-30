package com.phodal.shirelang.run.runner

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.provider.ide.LocationInteractionContext
import com.phodal.shirecore.provider.ide.LocationInteractionProvider
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

    fun editorInteraction() {
        val context = LocationInteractionContext(
            location = context.hole?.actionLocation ?: ShireActionLocation.INTENTION_MENU,
            interactionType = InteractionType.AppendCursorStream,
            editor = context.editor,
            project = context.myProject,
        )

        LocationInteractionProvider.provide(context)?.execute(context)
    }
}
