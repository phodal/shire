package com.phodal.shire.settings

import com.intellij.application.options.EditorFontsConstants
import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.MathUtil
import com.intellij.util.ui.JBDimension
import com.phodal.shire.settings.components.testLLMConnection
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class ShireSettingUi : ConfigurableUi<ShireSettingsState> {
    private var panel: JPanel? = null
    private var apiHost: JTextField = JTextField()
    private var modelName: JTextField = JTextField()
    private var engineToken: JTextField = JBPasswordField()

    private var temperatureField: JTextField = JTextField(4)
    private val minTemperature = 0.0f
    private val maxTemperature = 1.0f

    init {
        this.panel = panel {
            row("LLM API Host:") {
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


            row("Temperature:") {
                cell(temperatureField.apply {
                    this.text = "0.1"
                    this.addKeyListener(object : KeyAdapter() {
                        override fun keyPressed(e: KeyEvent) {
                            if (e.keyCode != KeyEvent.VK_UP && e.keyCode != KeyEvent.VK_DOWN) return
                            val up = e.keyCode == KeyEvent.VK_UP
                            try {
                                var value: Float = temperatureField.getText().toFloat()
                                value += (if (up) 0.1 else -0.1).toFloat()
                                value = MathUtil.clamp(value, minTemperature, maxTemperature)

                                if (value < minTemperature) {
                                    value = minTemperature
                                } else if (value > maxTemperature) {
                                    value = maxTemperature
                                }

                                temperatureField.text = value.toString()
                            } catch (ignored: NumberFormatException) {
                            }
                        }
                    })
                })
            }

            testLLMConnection(ProjectManager.getInstance().openProjects.firstOrNull())
        }
    }

    override fun reset(settings: ShireSettingsState) {
        apiHost.text = settings.apiHost
        modelName.text = settings.modelName
        engineToken.text = settings.apiToken
        temperatureField.text = settings.temperature.toString()
    }

    override fun isModified(settings: ShireSettingsState): Boolean {
        return apiHost.text != settings.apiHost ||
                modelName.text != settings.modelName ||
                engineToken.text != settings.apiToken ||
                temperatureField.text != settings.temperature.toString()
    }

    override fun apply(settings: ShireSettingsState) {
        settings.apiHost = apiHost.text ?: ""
        settings.modelName = modelName.text ?: ""
        settings.apiToken = engineToken.text ?: ""
        settings.temperature = temperatureField.text.toFloatOrNull() ?: 0.0f
    }

    override fun getComponent(): JComponent {
        return panel!!
    }
}