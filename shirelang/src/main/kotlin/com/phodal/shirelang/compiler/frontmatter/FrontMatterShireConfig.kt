package com.phodal.shirelang.compiler.frontmatter

import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType

data class FrontMatterShireConfig(
    val name: String,
    val description: String,
    val interaction: InteractionType,
    val actionLocation: ShireActionLocation,
) {
    companion object {
        fun from(fm: MutableMap<String, Map<FrontMatterType, String>>): FrontMatterShireConfig {
            val name = fm["name"]?.get(FrontMatterType.STRING) ?: ""
            val description = fm["description"]?.get(FrontMatterType.STRING) ?: ""
            val interaction = fm["interaction"]?.get(FrontMatterType.STRING) ?: ""
            val actionLocation = fm["actionLocation"]?.get(FrontMatterType.STRING) ?: ""

            return FrontMatterShireConfig(
                name,
                description,
                InteractionType.from(interaction),
                ShireActionLocation.from(actionLocation)
            )
        }
    }
}
