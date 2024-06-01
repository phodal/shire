package com.phodal.shirelang.run

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.phodal.shirelang.psi.ShireFile

class ShireRunConfigurationProducer : LazyRunConfigurationProducer<ShireConfiguration>() {
    override fun getConfigurationFactory() = ShireConfigurationType.getInstance()

    override fun setupConfigurationFromContext(
        configuration: ShireConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>,
    ): Boolean {
        val psiFile = sourceElement.get().containingFile as? ShireFile ?: return false
        val virtualFile = psiFile.virtualFile ?: return false

        configuration.name = virtualFile.presentableName
        configuration.setScriptPath(virtualFile.path)

        return true
    }

    override fun isConfigurationFromContext(
        configuration: ShireConfiguration,
        context: ConfigurationContext,
    ): Boolean {
        val psiLocation = context.psiLocation ?: return false
        val psiFile = psiLocation.containingFile as? ShireFile ?: return false
        val virtualFile = psiFile.virtualFile ?: return false

        return virtualFile.path == configuration.getScriptPath()
    }

}