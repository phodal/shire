package com.phodal.shirelang.compiler.frontmatter

import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType

sealed class SelectElementStrategy {
    /**
     * Auto select parent block element, like function, class, etc.
     */
    object DEFAULT : SelectElementStrategy()
}

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
    val data: Map<String, FrontMatterType> = mutableMapOf(),
    /**
     * The strategy to select the element to apply the action.
     * If not selected text, will according the element position to select the element block.
     * For example, if cursor in a function, select the function block.
     */
    val selectionStrategy: SelectElementStrategy = SelectElementStrategy.DEFAULT,
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
    val postProcessors: List<String> = emptyList(),

    /**
     * The list of rule files to apply for the action.
     */
    val filenameFilters: List<String> = emptyList(),

    /**
     * The list of rule files to apply for the action.
     */
    val fileContentFilters: List<String> = emptyList()
) : Smials {
    companion object {
        const val CONFIG_ID = "name"

        fun from(frontMatterMap: MutableMap<String, FrontMatterType>): HobbitHole? {
            val name = frontMatterMap[CONFIG_ID]?.value as? String ?: return null
            val description = frontMatterMap["description"]?.value as? String ?: ""
            val interaction = frontMatterMap["interaction"]?.value as? String ?: ""
            val actionLocation = frontMatterMap["actionLocation"]?.value as? String ?: ShireActionLocation.default()

            val data = mutableMapOf<String, FrontMatterType>()
            frontMatterMap.forEach { (key, value) ->
                if (key !in listOf(CONFIG_ID, "description", "interaction", "actionLocation")) {
                    data[key] = value
                }
            }

            return HobbitHole(
                name,
                description,
                InteractionType.from(interaction),
                ShireActionLocation.from(actionLocation),
                data
            )
        }
    }
}
