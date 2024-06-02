package com.phodal.shire.settings

import com.intellij.openapi.options.ConfigurableUi
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBDimension
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class ShireSettingUi : ConfigurableUi<ShireSettingsState> {
    private var panel: JPanel? = null
    private var apiHost: JTextField = JTextField().also {
    }
    private var modelName: JTextField = JTextField().also {
    }

    private var engineToken: JTextField = JBPasswordField().also {
    }

    init {
        this.panel = panel {
            row("API Host:") {
                cell(apiHost)
                    .applyToComponent { minimumSize = JBDimension(200, 1) }
                    .align(AlignX.FILL)
            }
            row("Model Name:") {
                cell(modelName)
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
        apiHost.text = settings.apiHost
        modelName.text = settings.modelName
        engineToken.text = settings.apiToken
    }

    override fun isModified(settings: ShireSettingsState): Boolean {
        return apiHost.text != settings.apiHost ||
                modelName.text != settings.modelName ||
                engineToken.text != settings.apiToken
    }

    override fun apply(settings: ShireSettingsState) {
        settings.apiHost = apiHost.text ?: ""
        settings.modelName = modelName.text ?: ""
        settings.apiToken = engineToken.text ?: ""
    }

    override fun getComponent(): JComponent {
        return panel!!
    }
}