package com.phodal.shirecore.provider.ide

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.config.InteractionType
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

    /**
     * the console view
     */
    val console: ConsoleView,
)