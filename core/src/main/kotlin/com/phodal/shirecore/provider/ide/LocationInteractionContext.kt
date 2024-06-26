package com.phodal.shirecore.provider.ide

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.action.ShireActionLocation
import com.phodal.shirecore.agent.InteractionType
import kotlinx.coroutines.flow.Flow

data class LocationInteractionContext(
    val location: ShireActionLocation,
    /**
     * the interaction type
     */
    val interactionType: InteractionType,
    /**
     * the LLM generate text stream, which can be used for [InteractionType.AppendCursorStream]
     */
    val streamText: Flow<String>? = null,

    val editor: Editor?,

    val project: Project,

    /**
     * the [com.phodal.shirecore.llm.ChatMessage]
     */
    val prompt: String,

    val selectElement: PsiElement? = null,
)