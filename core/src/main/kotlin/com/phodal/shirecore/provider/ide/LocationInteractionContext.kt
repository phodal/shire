package com.phodal.shirecore.provider.ide

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import kotlinx.coroutines.flow.Flow

data class LocationInteractionContext(
    val location: ShireActionLocation,
    val interactionType: InteractionType,
    /**
     * the LLM generate text stream, which can be used for [InteractionType.AppendCursorStream]
     */
    val streamText: Flow<String>,

    val editor: Editor?,

    val project: Project,
)