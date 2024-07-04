package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.provider.context.ActionLocationEditor
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.compiler.ShireCompiler
import com.phodal.shirelang.compiler.ShireTemplateCompiler
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConsoleView
import com.phodal.shirelang.run.ShireProcessHandler
import com.phodal.shirelang.run.executor.CustomRemoteAgentLlmExecutor
import com.phodal.shirelang.run.executor.ShireDefaultLlmExecutor
import com.phodal.shirelang.run.executor.ShireLlmExecutor
import com.phodal.shirelang.run.executor.ShireLlmExecutorContext

object ShireRunner {
    fun printCompiledOutput(
        console: ConsoleViewWrapperBase,
        promptText: String,
        shireConfiguration: ShireConfiguration,
    ) {
        console.print("Shire Script: ${shireConfiguration.getScriptPath()}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        console.print("Shire Script Compile output:\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        promptText.split("\n").forEach {
            when {
                it.contains(SHIRE_ERROR) -> {
                    console.print(it, ConsoleViewContentType.LOG_ERROR_OUTPUT)
                }

                else -> {
                    console.print(it, ConsoleViewContentType.USER_INPUT)
                }
            }
            console.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        console.print("\n--------------------\n", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    fun compileFinalPrompt(
        shireFile: ShireFile,
        project: Project,
        consoleView: ShireConsoleView?,
        shireConfiguration: ShireConfiguration,
        userInput: String,
    ): ShireRunnerContext {
        val compiler = ShireCompiler(project, shireFile, ActionLocationEditor.defaultEditor(project))
        val compileResult = compiler.compile()

        val variableTable = compileResult.variableTable

        val input = compileResult.shireOutput
        val hobbitHole = compileResult.config
        val locationEditor = ActionLocationEditor.provide(project, hobbitHole?.actionLocation)

        val templateCompiler = ShireTemplateCompiler(project, hobbitHole, variableTable, input)
        if (userInput.isNotEmpty()) {
            templateCompiler.putCustomVariable("input", userInput)
        }

        val promptText = templateCompiler.compile()
        printCompiledOutput(consoleView!!, promptText, shireConfiguration)

        // check prompt text had content or just new line with whitespace
        val promptTextTrim = promptText.trim()

        return ShireRunnerContext(hobbitHole, locationEditor, compileResult, promptTextTrim)
    }

    fun normalExecute(
        runData: ShireRunnerContext,
        processHandler: ShireProcessHandler,
        project: Project,
        console: ShireConsoleView?,
        configuration: ShireConfiguration,
    ) {
        val agent = runData.compileResult.executeAgent
        val shireLlmExecutorContext = ShireLlmExecutorContext(
            configuration = configuration,
            processHandler = processHandler,
            console = console!!,
            myProject = project,
            hole = runData.hole,
            prompt = runData.finalPrompt,
            editor = runData.editor,
        )
        val shireLlmExecutor: ShireLlmExecutor = when {
            agent != null -> {
                CustomRemoteAgentLlmExecutor(shireLlmExecutorContext, agent)
            }

            else -> {
                val isLocalMode = runData.compileResult.isLocalCommand
                ShireDefaultLlmExecutor(shireLlmExecutorContext, isLocalMode)
            }
        }

        shireLlmExecutor.prepareTask()
        shireLlmExecutor.execute { response, textRange ->
            var currentFile: PsiFile? = null
            runData.editor?.virtualFile?.also {
                currentFile = runReadAction { PsiManager.getInstance(project).findFile(it) }
            }

            val context = PostCodeHandleContext(
                selectedEntry = runData.hole?.pickupElement(project, runData.editor),
                currentLanguage = currentFile?.language,
                currentFile = currentFile,
                genText = response,
                modifiedTextRange = textRange,
                editor = runData.editor,
            )

            runData.hole?.executeStreamingEndProcessor(project, console, context)
            runData.hole?.executeAfterStreamingProcessor(project, console, context)
        }
    }
}
