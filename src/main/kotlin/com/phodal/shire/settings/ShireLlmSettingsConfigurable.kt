package com.phodal.shire.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.ConfigurableBase
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBDimension
import com.intellij.util.xmlb.XmlSerializerUtil
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ShireLlmSettingsConfigurable @JvmOverloads constructor(private val settings: ShireSettingsState = ShireSettingsState.getInstance())
    : ConfigurableBase<ShireSettingUi, ShireSettingsState>(
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

class ShireSettingUi : ConfigurableUi<ShireSettingsState> {
    private var panel: JPanel? = null
    private var openAiHost: JTextField = JTextField().also {
    }
    private var engineServer: JTextField = JTextField().also {
    }

    private var engineToken: JTextField = JBPasswordField().also {
    }

    init {
        this.panel = panel {
            row("OpenAI Host:") {
                cell(openAiHost)
                    .applyToComponent { minimumSize = JBDimension(200, 1) }
                    .align(AlignX.FILL)
            }
            row("Engine Server:") {
                cell(engineServer)
                    .applyToComponent { minimumSize = JBDimension(200, 1) }
                    .align(AlignX.FILL)
            }
            row("Engine Token:") {
                cell(engineToken)
                    .applyToComponent { minimumSize = JBDimension(200, 1) }
                    .align(AlignX.FILL)
            }
        }
    }

    override fun reset(settings: ShireSettingsState) {
        openAiHost.text = settings.openAiHost
        engineServer.text = settings.engineServer
        engineToken.text = settings.engineToken
    }

    override fun isModified(settings: ShireSettingsState): Boolean {
        return openAiHost.text != settings.openAiHost ||
                engineServer.text != settings.engineServer ||
                engineToken.text != settings.engineToken
    }

    override fun apply(settings: ShireSettingsState) {
        settings.openAiHost = openAiHost.text ?: ""
        settings.engineServer = engineServer.text ?: ""
        settings.engineToken = engineToken.text ?: ""
    }

    override fun getComponent(): JComponent {
        return panel!!
    }
}

@Service(Service.Level.APP)
@State(name = "com.phodal.shire.settings.ShireSettingsState", storages = [Storage("ShireSettings.xml")])
class ShireSettingsState : PersistentStateComponent<ShireSettingsState> {
    var openAiHost = ""
    var engineServer = ""
    var engineToken = ""

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
