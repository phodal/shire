package com.phodal.shirelang.run.runner

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project
import com.phodal.shire.llm.model.ChatMessage
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.runner.tasks.FileGenerateTask

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

    fun editorInteraction() {
        val msgs: List<ChatMessage> = listOf()
        val targetFile = context.editor?.virtualFile

        when (context.hole?.interaction) {
            InteractionType.AppendCursor -> TODO()
            InteractionType.AppendCursorStream -> TODO()
            InteractionType.OutputFile -> {
                // todo: replace real file name
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.myProject, msgs, fileName)
                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }
            InteractionType.ReplaceSelection -> {
            }
            InteractionType.ReplaceCurrentFile -> {
                // todo: replace real file name
                val fileName = targetFile?.name
                val task = FileGenerateTask(context.myProject, msgs, fileName)

                ProgressManager.getInstance()
                    .runProcessWithProgressAsynchronously(task, BackgroundableProcessIndicator(task))
            }
            InteractionType.InsertBeforeSelection -> {
                TODO()
            }

            null -> TODO()
        }
    }
}