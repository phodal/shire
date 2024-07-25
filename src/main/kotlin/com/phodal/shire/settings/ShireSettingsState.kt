package com.phodal.shire.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(name = "com.phodal.shire.settings.ShireSettingsState", storages = [Storage("ShireSettings.xml")])
class ShireSettingsState : PersistentStateComponent<ShireSettingsState> {
    var temperature: Float = 0.0f
    var apiHost = ""
    var modelName = ""
    var apiToken = ""

    @Synchronized
    override fun getState(): ShireSettingsState = this

    @Synchronized
    override fun loadState(state: ShireSettingsState) = XmlSerializerUtil.copyBean(state, this)

    companion object {
        fun getInstance(): ShireSettingsState {
            return ApplicationManager.getApplication().getService(ShireSettingsState::class.java).state
        }
    }
}