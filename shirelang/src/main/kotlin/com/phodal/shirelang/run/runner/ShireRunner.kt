package com.phodal.shirelang.run.runner

import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiManager
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.config.interaction.PostFunction
import com.phodal.shirecore.llm.LlmProvider
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.provider.action.TerminalLocationExecutor
import com.phodal.shirecore.provider.context.ActionLocationEditor
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.compiler.SHIRE_ERROR
import com.phodal.shirelang.compiler.ShireParsedResult
import com.phodal.shirelang.compiler.ShireTemplateCompiler
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConsoleView
import com.phodal.shirelang.run.ShireProcessHandler
import com.phodal.shirelang.run.executor.CustomRemoteAgentLlmExecutor
import com.phodal.shirelang.run.executor.ShireDefaultLlmExecutor
import com.phodal.shirelang.run.executor.ShireLlmExecutor
import com.phodal.shirelang.run.executor.ShireLlmExecutorContext
import com.phodal.shirelang.run.flow.ShireConversationService
import kotlinx.coroutines.*
import java.util.concurrent.CompletableFuture

class ShireRunner(
    private val shireFile: ShireFile,
    private val project: Project,
    private val console: ShireConsoleView?,
    private val configuration: ShireConfiguration,
    private val variableMap: Map<String, String>,
    private val processHandler: ShireProcessHandler,
) {
    private var compiledVariables: Map<String, Any> = mapOf()
    private val terminalLocationExecutor = TerminalLocationExecutor.provide(project)

    suspend fun execute(parsedResult: ShireParsedResult): String? {
        prepareExecute(parsedResult)

        val result = CompletableFuture<String>()

        val runnerContext = processTemplateCompile(parsedResult, variableMap, processHandler)
        if (runnerContext.hasError) return null

        this.compiledVariables = runnerContext.compiledVariables

        project.getService(ShireConversationService::class.java)
            .createConversation(configuration.getScriptPath(), runnerContext.compileResult)

        if (runnerContext.hole?.actionLocation == ShireActionLocation.TERMINAL_MENU) {
            executeTerminalUiTask(runnerContext) { response, textRange ->
                result.complete(response)
                executePostFunction(runnerContext, runnerContext.hole, response, textRange)
            }
        } else {
            executeNormalUiTask(runnerContext) { response, textRange ->
                result.complete(response)
                executePostFunction(runnerContext, runnerContext.hole, response, textRange)
            }
        }

        return withContext(Dispatchers.IO) {
            result.get()
        }
    }

    private fun executeTerminalUiTask(context: ShireRunnerContext, postFunction: PostFunction) {
        CoroutineScope(Dispatchers.Main).launch {
            val handler = terminalLocationExecutor?.bundler(project, variableMap["input"] ?: "",)
            if (handler == null) {
                console?.print("Terminal not found", ConsoleViewContentType.ERROR_OUTPUT)
                processHandler.destroyProcess()
                return@launch
            }

            val llmResult = StringBuilder()
            runBlocking {
                try {
                    LlmProvider.provider(project)?.stream(context.finalPrompt, "", false)?.collect {
                        llmResult.append(it)
                        handler.onChunk.invoke(it)
                    } ?: console?.print(
                        ShireBundle.message("shire.llm.notfound"),
                        ConsoleViewContentType.ERROR_OUTPUT
                    )
                } catch (e: Exception) {
                    console?.print(e.message ?: "Error", ConsoleViewContentType.ERROR_OUTPUT)
                    handler.onFinish?.invoke(null)
                    processHandler.detachProcess()
                }
            }

            val response = llmResult.toString()
            handler.onFinish?.invoke(response)

            postFunction(response, null)
            processHandler.detachProcess()
        }
    }

    private suspend fun processTemplateCompile(
        compileResult: ShireParsedResult, variableMap: Map<String, String>, shireProcessHandler: ShireProcessHandler
    ): ShireRunnerContext {
        val hobbitHole = compileResult.config

        val templateCompiler =
            ShireTemplateCompiler(project, hobbitHole, compileResult.variableTable, compileResult.shireOutput)

        variableMap.forEach { (key, value) ->
            templateCompiler.putCustomVariable(key, value)
        }

        val promptTextTrim = templateCompiler.compile().trim()
        val compiledVariables = templateCompiler.compiledVariables

        PostCodeHandleContext.getData()?.lastTaskOutput?.let {
            templateCompiler.putCustomVariable("output", it)
        }

        if (console != null) {
            printCompiledOutput(console, promptTextTrim, configuration)
        }

        var hasError = false

        if (promptTextTrim.isEmpty()) {
            console?.print("No content to run", ConsoleViewContentType.ERROR_OUTPUT)
            shireProcessHandler.destroyProcess()
            hasError = true
        }

        if (promptTextTrim.contains(SHIRE_ERROR)) {
            shireProcessHandler.exitWithError()
            hasError = true
        }

        return ShireRunnerContext(
            hobbitHole,
            editor = ActionLocationEditor.provide(project, hobbitHole?.actionLocation),
            compileResult,
            promptTextTrim,
            hasError,
            compiledVariables
        )
    }

    fun executeNormalUiTask(runData: ShireRunnerContext, postFunction: PostFunction) {
        val agent = runData.compileResult.executeAgent
        val hobbitHole = runData.hole

        val shireLlmExecutorContext = ShireLlmExecutorContext(
            configuration = configuration,
            processHandler = processHandler,
            console = console,
            myProject = project,
            hole = hobbitHole,
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

        shireLlmExecutor.execute(postFunction)
    }

    private fun executePostFunction(
        runnerContext: ShireRunnerContext,
        hobbitHole: HobbitHole?,
        response: String?,
        textRange: TextRange?,
    ) {
        val currentFile = runnerContext.editor?.virtualFile?.let {
            runReadAction { PsiManager.getInstance(project).findFile(it) }
        }

//        if (response != null && compiledVariables["output"] == null) {
//            compiledVariables = compiledVariables.plus("output" to response)
//        }

        val context = PostCodeHandleContext(
            selectedEntry = hobbitHole?.pickupElement(project, runnerContext.editor),
            currentLanguage = currentFile?.language,
            currentFile = currentFile,
            genText = response,
            modifiedTextRange = textRange,
            editor = runnerContext.editor,
            lastTaskOutput = response,
            compiledVariables = compiledVariables,
        )

        PostCodeHandleContext.putData(context)

        hobbitHole?.executeStreamingEndProcessor(project, console, context)
        hobbitHole?.executeAfterStreamingProcessor(project, console, context)
        processHandler.detachProcess()
    }

    private fun printCompiledOutput(
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

    fun prepareExecute(parsedResult: ShireParsedResult) {
        val hobbitHole = parsedResult.config
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val pickupElement = hobbitHole?.pickupElement(project, editor)
        val file = runReadAction {
            editor?.let { PsiManager.getInstance(project).findFile(it.virtualFile) }
        }

        val context = PostCodeHandleContext.getData() ?: PostCodeHandleContext(
            selectedEntry = pickupElement,
            currentLanguage = file?.language,
            currentFile = file,
            editor = editor,
            compiledVariables = compiledVariables,
        )
        PostCodeHandleContext.putData(context)
        hobbitHole?.setupStreamingEndProcessor(project, context)

    }
}
