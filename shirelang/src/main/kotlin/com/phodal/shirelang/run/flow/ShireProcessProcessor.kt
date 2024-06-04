package com.phodal.shirelang.run.flow

import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiUtilBase
import com.intellij.openapi.fileEditor.FileEditorManager
import com.phodal.shirecore.ShirelangNotifications
import com.phodal.shirelang.ShireLanguage
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireVisitor
import com.phodal.shirelang.utils.Code


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
    fun process(output: String, event: ProcessEvent, scriptPath: String) {
        conversationService.updateIdeOutput(scriptPath, output)

        val code = Code.parse(conversationService.getLlmResponse(scriptPath))
        val isShireCode = code.language == ShireLanguage
        if (isShireCode) {
            runInEdt {
                executeTask(ShireFile.fromString(project, code.text))
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
                        this.executeTask(newScript)
                    }
                }
            }
            event.exitCode != 0 -> {
                conversationService.tryFixWithLlm(scriptPath)
            }
        }
    }

    /**
     * This function is responsible for running a task with a new script.
     * @param newScript The new script to be run.
     */
    fun executeTask(newScript: ShireFile) {
        val devInsCompiler = createCompiler(project, newScript)
        val result = devInsCompiler.compile()
        if(result.output != "") {
            ShirelangNotifications.notify(project, result.output)
        }

        if (result.hasError) {
//            sendToChatWindow(project, ChatActionType.CHAT) { panel, service ->
//                service.handlePromptAndResponse(panel, object : ContextPrompter() {
//                    override fun displayPrompt(): String = result.output
//                    override fun requestPrompt(): String = result.output
//                }, null, true)
//            }
        }
        else {
            if (result.nextJob != null) {
                val nextJob = result.nextJob!!
                val nextResult = createCompiler(project, nextJob).compile()
                if(nextResult.output != "") {
                    ShirelangNotifications.notify(project, nextResult.output)
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
        text: String
    ): ShireCompiler {
        val devInFile = ShireFile.fromString(project, text)
        return createCompiler(project, devInFile)
    }

    private fun createCompiler(
        project: Project,
        devInFile: ShireFile
    ): ShireCompiler {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val element: PsiElement? = editor?.caretModel?.currentCaret?.offset?.let {
            val psiFile = PsiUtilBase.getPsiFileInEditor(editor, project) ?: return@let null
            getElementAtOffset(psiFile, it)
        }

        return ShireCompiler(project, devInFile, editor, element)
    }

    private fun getElementAtOffset(psiFile: PsiElement, offset: Int): PsiElement? {
        var element = psiFile.findElementAt(offset) ?: return null

        if (element is PsiWhiteSpace) {
            element = element.getParent()
        }

        return element
    }

    /**
     * 1. We need to call LLM to get the task list
     * 2. According to the input and output to decide the next step
     */
    fun createAgentTasks(): List<ShireFile> {
        TODO()
    }
}