package com.phodal.shirecore.provider.shire

import com.intellij.openapi.extensions.ExtensionPointName
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType

/**
 *
 * We need to provide 3 types of interactions:
 *
 * - Terminal, which can append stream in IDE terminal
 * - Editor, which can append stream in IDE editor
 * - CommitPanel, which can append stream in IDE commit panel
 */
interface LocationInteraction {
    fun isApplicable(interaction: ShireActionLocation, interactionType: InteractionType, context: Any): Boolean

    fun execute(interaction: ShireActionLocation, interactionType: InteractionType, context: Any)

    companion object {
        private val EP_NAME: ExtensionPointName<LocationInteraction> =
            ExtensionPointName("com.phodal.shireSymbolProvider")

        fun provide(location: ShireActionLocation, interaction: InteractionType, context: Any): LocationInteraction? {
            return EP_NAME.extensionList.firstOrNull {
                it.isApplicable(location, interaction, context)
            }
        }
    }
}
