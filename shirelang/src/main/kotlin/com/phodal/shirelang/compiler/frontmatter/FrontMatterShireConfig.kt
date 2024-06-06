package com.phodal.shirelang.compiler.frontmatter

import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType

sealed class ElementStrategy {
    /**
     * Auto select parent block element, like function, class, etc.
     */
    object DEFAULT : ElementStrategy()
}

/**
 * - Normal: the action is a normal action
 * - Flow: each action can be a task in a flow, which will build a DAG
 */
data class FrontMatterShireConfig(
    val name: String,
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
    val elementStrategy: ElementStrategy = ElementStrategy.DEFAULT,
    /**
     * The list of actions that this action depends on.
     * We use it for Directed Acyclic Graph (DAG) to represent dependencies between actions.
     *
     * todo: apply isApplicable for actions that do not depend on any tasks.
     */
    val dependsOn: List<String> = emptyList(),
    /**
     * Post middleware actions, like
     * Logging, Metrics, CodeVerify, RunCode, ParseCode etc.
     *
     */
    val postMiddleware: List<String> = emptyList(),
) {
    companion object {
        fun from(frontMatterMap: MutableMap<String, FrontMatterType>): FrontMatterShireConfig? {
            val name = frontMatterMap["name"]?.value as? String ?: return null
            val description = frontMatterMap["description"]?.value as? String ?: ""
            val interaction = frontMatterMap["interaction"]?.value as? String ?: ""
            val actionLocation = frontMatterMap["actionLocation"]?.value as? String ?: ShireActionLocation.default()

            val data = mutableMapOf<String, FrontMatterType>()
            frontMatterMap.forEach { (key, value) ->
                if (key !in listOf("name", "description", "interaction", "actionLocation")) {
                    data[key] = value
                }
            }

            return FrontMatterShireConfig(
                name,
                description,
                InteractionType.from(interaction),
                ShireActionLocation.from(actionLocation),
                data
            )
        }
    }
}
