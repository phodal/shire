package com.phodal.shirelang.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.config.ShireActionLocation
import com.phodal.shirecore.provider.ide.ShirePromptBuilder
import com.phodal.shirelang.actions.base.DynamicShireActionService

class ShireActionPromptBuilder : ShirePromptBuilder {
    override fun build(project: Project, actionLocation: String, originPrompt: String): String {
        val location: ShireActionLocation = ShireActionLocation.valueOf(actionLocation)

        val action = DynamicShireActionService.getInstance(project).getActions(location)
            .firstOrNull() ?: return originPrompt


        return action.shireFile.text?.replace("\$chatPrompt", originPrompt) ?: ""
    }
}
