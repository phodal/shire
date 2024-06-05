package com.phodal.shirelang.compiler.frontmatter

import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType

data class FrontMatterShireConfig(
    val name: String,
    val description: String,
    val interaction: InteractionType,
    val actionLocation: ShireActionLocation,
    val data: Map<String, FrontMatterType> = mutableMapOf(),
) {
    companion object {
        fun from(fm: MutableMap<String, FrontMatterType>): FrontMatterShireConfig? {
            val name = fm["name"]?.value as? String ?: return null
            val description = fm["description"]?.value as? String ?: ""
            val interaction = fm["interaction"]?.value as? String ?: ""
            val actionLocation = fm["actionLocation"]?.value as? String ?: ShireActionLocation.default()

            val data = mutableMapOf<String, FrontMatterType>()
            fm.forEach { (key, value) ->
                if (key != "name" && key != "description" && key != "interaction" && key != "actionLocation") {
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
