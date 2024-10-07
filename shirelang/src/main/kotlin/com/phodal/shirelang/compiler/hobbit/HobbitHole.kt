package com.phodal.shirelang.compiler.hobbit

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.config.InteractionType
import com.phodal.shirecore.console.isCanceled
import com.phodal.shirecore.middleware.PostProcessorContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessorFuncSign
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirecore.middleware.select.SelectedEntry
import com.phodal.shirecore.workerThread
import com.phodal.shirelang.compiler.parser.HobbitHoleParser
import com.phodal.shirelang.compiler.hobbit.base.Smials
import com.phodal.shirelang.compiler.hobbit.ast.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ast.MethodCall
import com.phodal.shirelang.compiler.hobbit.ast.action.PatternAction
import com.phodal.shirelang.compiler.hobbit.ast.TaskRoutes
import com.phodal.shirelang.compiler.hobbit.ast.action.DirectAction
import com.phodal.shirelang.compiler.hobbit.execute.FunctionStatementProcessor
import com.phodal.shirelang.compiler.hobbit.function.ForeignFunction
import com.phodal.shirelang.compiler.patternaction.VariableTransform
import com.phodal.shirelang.psi.ShireFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Hobbit Hole 用于定义 IDE 交互逻辑与用户数据的流处理。
 *
 * 示例
 * ```shire
 * ---
 * name: "Summary"
 * description: "Generate Summary"
 * interaction: AppendCursor
 * actionLocation: ContextMenu
 * ---
 * ```
 *
 */
open class HobbitHole(
    /**
     * Display name of the Shire command, will show in the IDE's UI base on [HobbitHole.interaction].
     *
     * For example: [ShireActionLocation.CONTEXT_MENU], will show in the context menu.
     *
     * ```shire
     * ---
     * name: "AutoTest"
     * ---
     * ```
     */
    val name: String,
    /**
     * Tooltips for the action, will show in Hover tips on the UI.
     *
     * ```shire
     * ---
     * description: "Generate Test"
     * ---
     * ```
     */
    val description: String? = null,
    /**
     * The output of the action can be in editor with streaming text when use use [InteractionType.AppendCursorStream
     *
     * ```shire
     * ---
     * interaction: AppendCursor
     * ---
     * ```
     */
    val interaction: InteractionType = InteractionType.RunPanel,
    /**
     * The location of the action, should be one of [ShireActionLocation], the default is [ShireActionLocation.RUN_PANEL].
     *
     * ```shire
     * ---
     * actionLocation: ContextMenu
     * ---
     * ```
     */
    val actionLocation: ShireActionLocation = ShireActionLocation.RUN_PANEL,
    /**
     * The strategy to select the element to apply the action.
     * If not selected text, will according the element position to select the element block.
     * For example, if cursor in a function, select the function block.
     *
     * ```shire
     * ---
     * selectionStrategy: "Block"
     * ---
     */
    val selectionStrategy: SelectElementStrategy? = null,

    /**
     * The list of variables with PatternAction for build the variable.
     *
     * ```shire
     * ---
     * variables:
     *   "name": "/[a-zA-Z]+/"
     *   "var2": /.*.java/ { grep("error.log") | sort | print }
     *   "testTemplate": /\(.*\).java/ {
     *     case "$1" {
     *       "Controller" { cat(".shire/templates/ControllerTest.java") }
     *       "Service" { cat(".shire/templates/ServiceTest.java") }
     *       default  { cat(".shire/templates/DefaultTest.java") }
     *     }
     *   }
     *
     * ---
     */
    val variables: MutableMap<String, VariableTransform> = mutableMapOf(),

    /**
     * This code snippet declares a variable 'when_' of type List<VariableCondition> and initializes it with an empty list.
     * 'when_' is a list that stores VariableCondition objects.
     *
     * Which is used for: [com.intellij.codeInsight.intention.IntentionAction.isAvailable], [com.intellij.openapi.project.DumbAwareAction.DumbAwareAction.update] to check is show menu.
     *
     * ```shire
     * ---
     * when: { $filePath.contains("src/main/java") && $fileName.contains(".java") }
     * ---
     * ```
     */
    val when_: FrontMatterType.EXPRESSION? = null,

    /**
     * This property represents a list of post-middleware actions to be executed after the streaming process ends.
     * It allows for the definition of various operations such as logging, metrics collection, code verification,
     * execution of code, or parsing code, among others.
     *
     * ```shire
     * ---
     * onStreamingEnd: { parseCode | saveFile("docs/shire/shire-context-variable.md")  }
     * ---
     * ```
     */
    val onStreamingEnd: List<PostProcessorFuncSign> = emptyList(),

    /**
     * TBD, keep it for future use.
     */
    @Deprecated("TBD")
    val onStreaming: List<PostProcessor> = emptyList(),

    /**
     * ```shire
     * ---
     * beforeStreaming: { parseCode}
     * ---
     * ```
     */
    val beforeStreaming: DirectAction? = null,

    /**
     * The list of actions that this action depends on.
     *
     * ```shire
     * ---
     * afterStreaming: {
     *     condition {
     *       "variable-success" { $selection.length > 1 }
     *       "jsonpath-success" { jsonpath("/bookstore/book[price>35]") }
     *     }
     *     case condition {
     *       "variable-sucesss" { done }
     *       "jsonpath-success" { TODO }
     *       default { TODO }
     *     }
     *   }
     * ---
     * ```
     */
    val afterStreaming: TaskRoutes? = null,

    /**
     * The IDE shortcut for the action, which use the IntelliJ IDEA's shortcut format.
     *
     * ```shire
     * ---
     * shortcut: "meta pressed V"
     * ---
     * ```
     */
    val shortcut: KeyboardShortcut? = null,

    /**
     * the status of the action, default is true.
     *
     * ```shire
     * ---
     * enabled: false
     * ---
     */
    val enabled: Boolean = true,

    /**
     * the LLM model for action, default is null which will use the default model.
     *
     * ```shire
     * ---
     * model: "default"
     * ---
     *
     */
    val model: String? = null,

    /**
     * Custom Functions for the action.
     */
    val functions: MutableMap<String, ForeignFunction> = mutableMapOf(),

    /**
     * The rest of the data.
     */
    val userData: Map<String, FrontMatterType> = mutableMapOf(),
) : Smials {
    fun pickupElement(project: Project, editor: Editor?): SelectedEntry? {
        return runReadAction {
            this.selectionStrategy?.select(project, editor)
            return@runReadAction selectionStrategy?.getSelectedElement(project, editor)
        }
    }

    fun setupStreamingEndProcessor(project: Project, context: PostProcessorContext) {
        onStreamingEnd.forEach { funcNode ->
            PostProcessor.handler(funcNode.funcName)?.setup(context)
        }
    }

    fun executeStreamingEndProcessor(
        project: Project,
        console: ConsoleView?,
        context: PostProcessorContext,
        compiledVariables: Map<String, Any>,
    ): String? {
        console?.print("\n", ConsoleViewContentType.SYSTEM_OUTPUT)
        onStreamingEnd.forEach { funcNode ->
            if (console?.isCanceled() == true) return@forEach
            console?.print("execute streamingEnd: ${funcNode.funcName}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
            val postProcessor = PostProcessor.handler(funcNode.funcName)
            if (postProcessor == null) {
                // TODO: change execute
                console?.print("Not found function: ${funcNode.funcName}\n", ConsoleViewContentType.SYSTEM_OUTPUT)
                return@forEach
            }

            val args: List<Any> = funcNode.args.map { arg ->
                when (arg) {
                    is String -> {
                        if (arg.startsWith("$")) {
                            if (arg == "\$output" && context.lastTaskOutput != null) {
                                context.lastTaskOutput ?: "\$output"
                            } else {
                                compiledVariables[arg.substring(1)] ?: ""
                            }
                        } else {
                            arg
                        }
                    }

                    else -> arg
                }
            }

            val lastResult = postProcessor.execute(project, context, console, args)
            context.lastTaskOutput = lastResult as? String
        }

        return context.lastTaskOutput
    }

    fun executeBeforeStreamingProcessor(
        myProject: Project,
        context: PostProcessorContext,
        console: ConsoleView?,
        compiledVariables: MutableMap<String, Any?>,
    ): Any? {
        if (console?.isCanceled() == true) return null
        if (beforeStreaming == null) return null
        if (beforeStreaming.processors.isEmpty()) return null

        CoroutineScope(workerThread).launch {
            FunctionStatementProcessor(myProject, this@HobbitHole).execute(
                beforeStreaming.processors,
                compiledVariables
            )
        }

        return context.lastTaskOutput
    }

    fun executeAfterStreamingProcessor(
        myProject: Project,
        console: ConsoleView?,
        context: PostProcessorContext,
    ): Any? {
        if (console?.isCanceled() == true) return null
        val result = afterStreaming?.execute(myProject, context, this)
        context.lastTaskOutput = result as? String
        return result
    }

    companion object {
        const val NAME = "name"
        const val ACTION_LOCATION = "actionLocation"
        const val INTERACTION = "interaction"
        const val STRATEGY_SELECTION = "selectionStrategy"

        const val ON_STREAMING_END = "onStreamingEnd"
        const val BEFORE_STREAMING = "beforeStreaming"
        const val AFTER_STREAMING = "afterStreaming"
        const val ON_STREAMING = "onStreaming"

        const val ENABLED = "enabled"
        const val MODEL = "model"

        private const val DESCRIPTION = "description"
        private const val VARIABLES = "variables"
        private const val FUNCTIONS = "functions"

        private const val WHEN = "when"
        private const val SHORTCUT = "shortcut"

        fun from(file: ShireFile): HobbitHole? {
            return HobbitHoleParser.parse(file)
        }

        /**
         * For Code completion ,
         * todo: modify to map with description
         */
        fun keys(): Map<String, String> {
            return mapOf(
                NAME to "The display name of the action",
                DESCRIPTION to "The tips for the action",
                WHEN to "The condition to run the action",

                INTERACTION to "The output of the action can be a file, a string, etc.",
                ACTION_LOCATION to "The location of the action, can [ShireActionLocation]",
                SHORTCUT to "The shortcut for the action",

                STRATEGY_SELECTION to "The strategy to select the element to apply the action",
                VARIABLES to "The list of variables to apply for the action",
                FUNCTIONS to "The list of custom functions for the action",

                ON_STREAMING to "TBD ",
                ON_STREAMING_END to "After Streaming end middleware actions, like Logging, Metrics, CodeVerify, RunCode, ParseCode etc.",
                BEFORE_STREAMING to "The task/patternAction before streaming",
                AFTER_STREAMING to "Decision to run the task after streaming, routing to different tasks",
            )
        }

        fun from(frontMatterMap: MutableMap<String, FrontMatterType>): HobbitHole {
            val name = frontMatterMap[NAME]?.value as? String ?: ""
            val description = frontMatterMap[DESCRIPTION]?.value as? String ?: ""
            val interaction = frontMatterMap[INTERACTION]?.value as? String ?: ""
            val actionLocation = frontMatterMap[ACTION_LOCATION]?.value as? String ?: ShireActionLocation.default()
            val enabled = frontMatterMap[ENABLED]?.value as? Boolean ?: true
            val model = frontMatterMap[MODEL]?.value as? String

            val shortcut = (frontMatterMap[SHORTCUT]?.value as? String)?.let {
                KeyboardShortcut.fromString(it)
            }

            val data = mutableMapOf<String, FrontMatterType>()
            frontMatterMap.forEach { (key, value) ->
                if (key !in listOf(NAME, DESCRIPTION, INTERACTION, ACTION_LOCATION)) {
                    data[key] = value
                }
            }

            val selectionStrategy = SelectElementStrategy.fromString(
                frontMatterMap[STRATEGY_SELECTION]?.value as? String ?: ""
            )

            val endProcessors = frontMatterMap[ON_STREAMING_END]?.let {
                buildStreamingEndProcessors(it)
            } ?: mutableListOf()

            val variables = (frontMatterMap[VARIABLES] as? FrontMatterType.OBJECT)?.let {
                buildVariableTransformations(it.toValue())
            } ?: mutableMapOf()

            val functions: MutableMap<String, ForeignFunction> =
                (frontMatterMap[FUNCTIONS] as? FrontMatterType.OBJECT)?.let {
                    ForeignFunction.from(it.toValue())
                }.orEmpty().associateBy { it.funcName }.toMutableMap()

            val beforeStreaming: DirectAction? = if (frontMatterMap[BEFORE_STREAMING] != null) {
                DirectAction.from(frontMatterMap[BEFORE_STREAMING]!!)
            } else {
                null
            }

            val afterStreaming: TaskRoutes? = (frontMatterMap[AFTER_STREAMING] as? FrontMatterType.ARRAY)?.let {
                try {
                    TaskRoutes.from(it)
                } catch (e: Exception) {
                    logger<HobbitHole>().warn("Error to parse after streaming: $e")
                    null
                }
            }

            val whenCondition = frontMatterMap[WHEN] as? FrontMatterType.EXPRESSION

            return HobbitHole(
                name = name,
                description = description,
                interaction = InteractionType.from(interaction),
                actionLocation = ShireActionLocation.from(actionLocation),
                selectionStrategy = selectionStrategy,
                variables = variables,
                functions = functions,
                userData = data,
                when_ = whenCondition,
                beforeStreaming = beforeStreaming,
                onStreamingEnd = endProcessors,
                afterStreaming = afterStreaming,
                shortcut = shortcut,
                enabled = enabled,
                model = model
            )
        }

        private fun buildVariableTransformations(variableObject: Map<String, FrontMatterType>): MutableMap<String, VariableTransform> {
            return variableObject.mapNotNull { (key, value) ->
                val variable = key.removeSurrounding("\"")
                PatternAction.from(value)?.let {
                    val pattern = it.pattern.removeSurrounding("/")
                    VariableTransform(variable, pattern, it.patternFuncs, it.isQueryStatement)
                }
            }.associateBy { it.variable }.toMutableMap()
        }

        private fun buildStreamingEndProcessors(item: FrontMatterType): List<PostProcessorFuncSign> {
            val endProcessors: MutableList<PostProcessorFuncSign> = mutableListOf()
            when (item) {
                is FrontMatterType.ARRAY -> {
                    item.toValue().forEach { matterType ->
                        when (matterType) {
                            is FrontMatterType.EXPRESSION -> {
                                val processorNode = toPostProcessorNode(matterType)
                                endProcessors.add(processorNode)
                            }

                            else -> {}
                        }
                    }
                }

                is FrontMatterType.STRING -> {
                    val handleName = item.value as String
                    endProcessors.add(PostProcessorFuncSign(handleName, emptyList()))
                }

                else -> {}
            }

            return endProcessors
        }

        private fun toPostProcessorNode(expression: FrontMatterType.EXPRESSION): PostProcessorFuncSign {
            return when (val child = expression.value) {
                is MethodCall -> {
                    val handleName = child.objectName.display()
                    val args: List<String> = child.arguments?.map { it.toString() } ?: emptyList()
                    return PostProcessorFuncSign(handleName, args)
                }

                else -> {
                    val handleName = expression.display()
                    PostProcessorFuncSign(handleName, emptyList())
                }
            }
        }
    }
}
