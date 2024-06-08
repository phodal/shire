package com.phodal.shirelang.compiler.hobbit

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import com.phodal.shirecore.middleware.PostCodeHandleContext
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.FrontmatterParser
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
     * The data of the action.
     */
    val additionalData: Map<String, FrontMatterType> = mutableMapOf(),
    /**
     * The strategy to select the element to apply the action.
     * If not selected text, will according the element position to select the element block.
     * For example, if cursor in a function, select the function block.
     */
    private val selectionStrategy: SelectElementStrategy = SelectElementStrategy.Blocked,
    /**
     * The list of actions that this action depends on.
     * We use it for Directed Acyclic Graph (DAG) to represent dependencies between actions.
     *
     * todo: apply isApplicable for actions that do not depend on any tasks.
     */
    val dependencies: List<String> = emptyList(),
    /**
     * Post middleware actions, like
     * Logging, Metrics, CodeVerify, RunCode, ParseCode etc.
     *
     */
    val postProcessors: List<PostProcessor> = emptyList(),

    /**
     * The list of rule files to apply for the action.
     */
    val filenameFilters: List<String> = emptyList(),

    /**
     * The list of rule files to apply for the action.
     */
    val fileContentFilters: List<String> = emptyList(),
) : Smials {
    fun pickupElement() {
        this.selectionStrategy.select()
    }

    fun setupProcessor(project: Project, editor: Editor?, file: PsiFile?) {
        val language = file?.language?.id
        val context = PostCodeHandleContext(null, language, file)
        postProcessors.forEach {
            it.setup(context)
        }
    }

    companion object {
        const val CONFIG_ID = "name"
        const val ACTION_LOCATION = "actionLocation"

        fun from(file: ShireFile): HobbitHole? {
            return FrontmatterParser.parse(file)
        }

        /**
         * For Code completion ,
         * todo: modify to map with description
         */
        fun keys(): Map<String, String> {
            return mapOf(
                CONFIG_ID to "The display name of the action",
                "description" to "The tips for the action",
                "interaction" to "The output of the action can be a file, a string, etc.",
                ACTION_LOCATION to "The location of the action, can [ShireActionLocation]",
                "selectionStrategy" to "The strategy to select the element to apply the action",
                "postProcessors" to "The list of post processors",
            )
        }

        fun from(frontMatterMap: MutableMap<String, FrontMatterType>): HobbitHole? {
            val name = frontMatterMap[CONFIG_ID]?.value as? String ?: return null
            val description = frontMatterMap["description"]?.value as? String ?: ""
            val interaction = frontMatterMap["interaction"]?.value as? String ?: ""
            val actionLocation = frontMatterMap[ACTION_LOCATION]?.value as? String ?: ShireActionLocation.default()

            val data = mutableMapOf<String, FrontMatterType>()
            frontMatterMap.forEach { (key, value) ->
                if (key !in listOf(CONFIG_ID, "description", "interaction", "actionLocation")) {
                    data[key] = value
                }
            }

            val selectionStrategy = frontMatterMap["selectionStrategy"]?.value as? String ?: ""

            val postProcessors: List<PostProcessor> = emptyList()
            frontMatterMap["postProcessors"]?.value?.let {
                PostProcessor.handler(it as String)
            }

            return HobbitHole(
                name,
                description,
                InteractionType.from(interaction),
                ShireActionLocation.from(actionLocation),
                additionalData = data,
                selectionStrategy = SelectElementStrategy.fromString(selectionStrategy),
                postProcessors = postProcessors
            )
        }
    }
}
