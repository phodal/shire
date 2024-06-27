package com.phodal.shirelang.compiler.hobbit

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.PostProcessorNode
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirecore.middleware.select.SelectedEntry
import com.phodal.shirelang.compiler.FrontmatterParser
import com.phodal.shirelang.compiler.hobbit._base.Smials
import com.phodal.shirelang.compiler.hobbit.ast.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ast.PatternAction
import com.phodal.shirelang.compiler.hobbit.ast.RuleBasedPatternAction
import com.phodal.shirelang.compiler.hobbit.ast.TaskRoutes
import com.phodal.shirelang.compiler.patternaction.PatternActionTransform
import com.phodal.shirelang.psi.ShireFile

/**
 * - Normal: the action is a normal action
 * - Flow: each action can be a task in a flow, which will build a DAG
 */
open class HobbitHole(
    /**
     * Display name of the action.
     */
    val name: String,
    /**
     * Tips for the action.
     */
    val description: String,
    /**
     * The output of the action can be a file, a string, etc.
     */
    val interaction: InteractionType,
    /**
     * The location of the action, can [ShireActionLocation]
     */
    val actionLocation: ShireActionLocation,
    /**
     * The strategy to select the element to apply the action.
     * If not selected text, will according the element position to select the element block.
     * For example, if cursor in a function, select the function block.
     */
    val selectionStrategy: SelectElementStrategy = SelectElementStrategy.Blocked,

    /**
     * The list of variables to apply for the action.
     */
    val variables: MutableMap<String, PatternActionTransform> = mutableMapOf(),

    /**
     * This code snippet declares a variable 'when_' of type List<VariableCondition> and initializes it with an empty list.
     * 'when_' is a list that stores VariableCondition objects.
     *
     * Which is used for: [IntentionAction.isAvailable], [DumbAwareAction.update] to check is show menu.
     */
    val when_: FrontMatterType.EXPRESSION? = null,

    /**
     * The list of rule files to apply for the action.
     */
    val ruleBasedFilter: List<RuleBasedPatternAction> = emptyList(),

    /**
     * This property represents a list of post-middleware actions to be executed after the streaming process ends.
     * It allows for the definition of various operations such as logging, metrics collection, code verification,
     * execution of code, or parsing code, among others.
     */
    val onStreamingEnd: List<PostProcessorNode> = emptyList(),

    /**
     * TBD, keep it for future use.
     */
    @Deprecated("TBD")
    val onStreaming: List<PostProcessor> = emptyList(),

    /**
     * The list of actions that this action depends on.
     */
    val afterStreaming: TaskRoutes? = null,

    /**
     * The rest of the data.
     */
    val userData: Map<String, FrontMatterType> = mutableMapOf(),
) : Smials {
    fun pickupElement(project: Project, editor: Editor?): SelectedEntry? {
        this.selectionStrategy.select(project, editor)
        return selectionStrategy.getSelectedElement(project, editor)
    }

    fun setupStreamingEndProcessor(project: Project, context: PostCodeHandleContext) {
        onStreamingEnd.forEach { funcNode ->
            PostProcessor.handler(funcNode.funName)?.setup(context)
        }
    }

    fun executeStreamingEndProcessor(
        project: Project,
        console: ConsoleView?,
        context: PostCodeHandleContext,
    ) {
        onStreamingEnd.forEach { funcNode ->
            PostProcessor.handler(funcNode.funName)?.execute(project, context, console)
        }
    }

    fun executeAfterStreamingProcessor(myProject: Project, console: ConsoleView?, context: PostCodeHandleContext) {
        // todo
    }

    companion object {
        const val NAME = "name"
        const val ACTION_LOCATION = "actionLocation"
        const val INTERACTION = "interaction"
        const val STRATEGY_SELECTION = "selectionStrategy"
        const val ON_STREAMING_END = "onStreamingEnd"
        const val AFTER_STREAMING = "afterStreaming"
        const val ON_STREAMING = "onStreaming"
        private const val DESCRIPTION = "description"
        private const val FILENAME_RULES = "filenameRules"
        private const val VARIABLES = "variables"
        const val WHEN = "when"

        fun from(file: ShireFile): HobbitHole? {
            return FrontmatterParser.parse(file)
        }

        /**
         * For Code completion ,
         * todo: modify to map with description
         */
        fun keys(): Map<String, String> {
            return mapOf(
                NAME to "The display name of the action",
                DESCRIPTION to "The tips for the action",
                INTERACTION to "The output of the action can be a file, a string, etc.",
                ACTION_LOCATION to "The location of the action, can [ShireActionLocation]",
                STRATEGY_SELECTION to "The strategy to select the element to apply the action",
                FILENAME_RULES to "Custom prompt based on the file name",
                ON_STREAMING_END to "After Streaming end middleware actions, like Logging, Metrics, CodeVerify, RunCode, ParseCode etc.",
                AFTER_STREAMING to "Decision to run the task after streaming, routing to different tasks",
            )
        }

        fun from(frontMatterMap: MutableMap<String, FrontMatterType>): HobbitHole {
            val name = frontMatterMap[NAME]?.value as? String ?: ""
            val description = frontMatterMap[DESCRIPTION]?.value as? String ?: ""
            val interaction = frontMatterMap[INTERACTION]?.value as? String ?: ""
            val actionLocation = frontMatterMap[ACTION_LOCATION]?.value as? String ?: ShireActionLocation.default()

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

            val filenameRules = (frontMatterMap[FILENAME_RULES] as? FrontMatterType.OBJECT)?.let {
                buildFilenameRules(it.toValue())
            } ?: mutableListOf()

            val variables = (frontMatterMap[VARIABLES] as? FrontMatterType.OBJECT)?.let {
                buildVariableTransformations(it.toValue())
            } ?: mutableMapOf()

            val afterStreaming: TaskRoutes? = (frontMatterMap[AFTER_STREAMING] as? FrontMatterType.ARRAY)?.let {
                try {
                    TaskRoutes.from(it)
                } catch (e: Exception) {
                    null
                }
            }

            val whenCondition = frontMatterMap[WHEN] as? FrontMatterType.EXPRESSION

            return HobbitHole(
                name,
                description,
                InteractionType.from(interaction),
                ShireActionLocation.from(actionLocation),
                selectionStrategy = selectionStrategy,
                variables = variables,
                userData = data,
                when_ = whenCondition,
                ruleBasedFilter = filenameRules,
                onStreamingEnd = endProcessors,
                afterStreaming = afterStreaming
            )
        }

        private fun buildFilenameRules(obj: Map<String, FrontMatterType>): List<RuleBasedPatternAction> {
            return obj.mapNotNull { (key, value) ->
                val text = key.removeSurrounding("\"")
                PatternAction.from(value)?.let {
                    RuleBasedPatternAction(text, it.patternFuncs)
                }
            }
        }

        private fun buildVariableTransformations(variableObject: Map<String, FrontMatterType>): MutableMap<String, PatternActionTransform> {
            return variableObject.mapNotNull { (key, value) ->
                val variable = key.removeSurrounding("\"")
                PatternAction.from(value)?.let {
                    val pattern = it.pattern.removeSurrounding("/")
                    PatternActionTransform(variable, pattern, it.patternFuncs, it.isQueryStatement)
                }
            }.associateBy { it.variable }.toMutableMap()
        }

        private fun buildStreamingEndProcessors(item: FrontMatterType): List<PostProcessorNode> {
            val endProcessors: MutableList<PostProcessorNode> = mutableListOf()
            when (item) {
                is FrontMatterType.ARRAY -> {
                    item.toValue().forEach { matterType ->
                        when (matterType) {
                            is FrontMatterType.EXPRESSION -> {
                                val handleName = toFuncName(matterType)
                                endProcessors.add(PostProcessorNode(handleName, emptyList()))
                            }

                            else -> {}
                        }
                    }
                }

                is FrontMatterType.STRING -> {
                    val handleName = item.value as String
                    endProcessors.add(PostProcessorNode(handleName, emptyList()))
                }

                else -> {}
            }

            return endProcessors
        }

        private fun toFuncName(expression: FrontMatterType.EXPRESSION): String {
            return expression.display()
        }
    }
}
