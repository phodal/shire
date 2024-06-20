package com.phodal.shirelang.run.flow

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase
import com.intellij.openapi.fileEditor.FileEditorManager
import com.phodal.shire.llm.LlmProvider
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.ShireLanguage
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireVisitor
import com.phodal.shirelang.run.ShireConsoleView
import com.phodal.shirelang.utils.Code
import kotlinx.coroutines.runBlocking


@Service(Service.Level.PROJECT)
class ShireProcessProcessor(val project: Project) {
    private val conversationService = project.getService(ShireConversationService::class.java)

    /**
     * This function takes a ShireFile as input and returns a list of PsiElements that are comments.
     * It iterates through the ShireFile and adds any comments it finds to the list.
     *
     * @param devInFile the ShireFile to search for comments
     * @return a list of PsiElements that are comments
     */
    private fun lookupFlagComment(devInFile: ShireFile): List<PsiElement> {
        val comments = mutableListOf<PsiElement>()
        devInFile.accept(object : ShireVisitor() {
            override fun visitComment(comment: PsiComment) {
                comments.add(comment)
            }
        })

        return comments
    }

    /**
     * Process the output of a script based on the exit code and flag comment.
     * If LLM returns a Shire code, execute it.
     * If the exit code is not 0, attempts to fix the script with LLM.
     * If the exit code is 0 and there is a flag comment, process it.
     *
     * Flag comment format:
     * ```shire
     * [flow]:flowable.devin, means next step is flowable.devin
     * ```
     *
     * @param output The output of the script
     * @param event The process event containing the exit code
     * @param scriptPath The path of the script file
     */
    fun process(output: String, event: ProcessEvent, scriptPath: String, consoleView: ShireConsoleView?) {
        conversationService.updateIdeOutput(scriptPath, output)

        val code = Code.parse(conversationService.getLlmResponse(scriptPath))
        val isShireCode = code.language == ShireLanguage.INSTANCE
        if (isShireCode) {
            runInEdt {
                executeTask(ShireFile.fromString(project, code.text), consoleView)
            }
        }

        when {
            event.exitCode == 0 -> {
                val devInFile: ShireFile? = runReadAction { ShireFile.lookup(project, scriptPath) }
                val comment = lookupFlagComment(devInFile!!).firstOrNull() ?: return
                if (comment.textRange.startOffset == 0) {
                    val text = comment.text
                    if (text.startsWith("[flow]:")) {
                        val nextScript = text.substring(7)
                        val newScript = ShireFile.lookup(project, nextScript) ?: return
                        this.executeTask(newScript, consoleView)
                    }
                }
            }

            event.exitCode != 0 -> {
                conversationService.tryFixWithLlm(scriptPath, consoleView)
            }
        }
    }

    /**
     * This function is responsible for running a task with a new script.
     * @param newScript The new script to be run.
     */
    private fun executeTask(newScript: ShireFile, consoleView: ShireConsoleView?) {
        val devInsCompiler = createCompiler(project, newScript)
        val result = devInsCompiler.compile()
        if (result.shireOutput != "") {
            ShirelangNotifications.notify(project, result.shireOutput)
        }

        if (result.hasError) {
            if (consoleView != null) {
                runBlocking {
                    LlmProvider.provider(project)?.stream(result.shireOutput, "Shirelang", true)
                        ?.collect {
                            consoleView.print(it, ConsoleViewContentType.NORMAL_OUTPUT)
                        }
                }
            }
        } else {
            if (result.nextJob != null) {
                val nextJob = result.nextJob!!
                val nextResult = createCompiler(project, nextJob).compile()
                if (nextResult.shireOutput != "") {
                    ShirelangNotifications.notify(project, nextResult.shireOutput)
                }
            }
        }
    }

    /**
     * Creates a new instance of `ShiresCompiler`.
     *
     * @param project The current project.
     * @param text The source code text.
     * @return A new instance of `ShiresCompiler`.
     */
    private fun createCompiler(
        project: Project,
        text: String,
    ): ShireCompiler {
        val devInFile = ShireFile.fromString(project, text)
        return createCompiler(project, devInFile)
    }

    private fun createCompiler(
        project: Project,
        devInFile: ShireFile,
    ): ShireCompiler {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val element: PsiElement? = editor?.caretModel?.currentCaret?.offset?.let {
            val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project) ?: return@let null
            SelectElementStrategy.getElementAtOffset(psiFile, it)
        }

        return ShireCompiler(project, devInFile, editor, element)
    }

    /**
     * 1. We need to call LLM to get the task list
     * 2. According to the input and output to decide the next step
     */
    fun createAgentTasks(): List<ShireFile> {
        TODO()
    }

}