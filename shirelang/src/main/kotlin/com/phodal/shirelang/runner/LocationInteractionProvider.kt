package com.phodal.shirelang.runner

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import kotlinx.coroutines.flow.Flow
import com.phodal.shirelang.compiler.hobbit.HobbitHole

/**
 * Interface for managing interactions in different IDE locations.
 * The interactions are categorized into three types:
 * - Terminal: Appends stream in the IDE terminal
 * - Editor: Appends stream in the IDE editor
 * - CommitPanel: Appends stream in the IDE commit panel
 */
interface LocationInteractionProvider {
    fun isApplicable(context: LocationInteractionContext): Boolean

    fun execute(context: LocationInteractionContext)

    companion object {
        private val EP_NAME: ExtensionPointName<LocationInteractionProvider> =
            ExtensionPointName("com.phodal.shireSymbolProvider")

        fun provide(context: LocationInteractionContext): LocationInteractionProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isApplicable(context)
            }
        }
    }
}

data class LocationInteractionContext(
    val location: ShireActionLocation,
    val interactionType: InteractionType,
    /**
     * the LLM generate text stream, which can be used for [InteractionType.AppendCursorStream]
     */
    val streamText: Flow<String>,

    val editor: Editor?,

    val hole: HobbitHole?,

    val project: Project,
)