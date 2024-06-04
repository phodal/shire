package com.phodal.shirelang.run

import com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.SimpleConfigurationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NotNullLazyValue
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.ShireLanguage

class ShireConfigurationType : SimpleConfigurationType(
    "ShiresConfigurationType",
    ShireLanguage.INSTANCE.id,
    ShireBundle.message("shire.line.marker.run.0", ShireLanguage.INSTANCE.id),
    NotNullLazyValue.lazy { ShireIcons.DEFAULT }
) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration =
        ShireConfiguration(project, this, "ShireConfiguration")

    companion object {
        fun getInstance(): ShireConfigurationType {
            return findConfigurationType(ShireConfigurationType::class.java)
        }
    }
}
