package com.phodal.shire.settings

import com.intellij.openapi.options.ConfigurableBase

class ShireLlmSettingsConfigurable @JvmOverloads constructor(private val settings: ShireSettingsState = ShireSettingsState.getInstance()) :
    ConfigurableBase<ShireSettingUi, ShireSettingsState>(
        "com.phodal.shire.settings",
        "Shire Settings",
        "com.phodal.shire.settings"
    ) {
    override fun getSettings(): ShireSettingsState {
        return ShireSettingsState.getInstance()
    }

    override fun createUi(): ShireSettingUi {
        return ShireSettingUi()
    }
}

