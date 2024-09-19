package com.phodal.shire.settings

import com.intellij.openapi.options.ConfigurableUi
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.AlignY
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.MathUtil
import com.intellij.util.ui.JBDimension
import com.phodal.shirecore.ShireCoreBundle
import com.phodal.shirecore.ShireCoroutineScope
import com.phodal.shirecore.llm.LlmConfig
import com.phodal.shirecore.llm.LlmProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.*

class ShireSettingUi : ConfigurableUi<ShireSettingsState> {
    private var panel: JPanel? = null
    private var apiHost: JTextField = JTextField()
    private var modelName: JTextField = JTextField()
    private var engineToken: JTextField = JBPasswordField()

    private var temperatureField: JTextField = JTextField(4)
    private val minTemperature = 0.0f
    private val maxTemperature = 1.0f

    private val testConnectionButton = JButton("Test LLM Connection")
    private val testResultField = JTextPane()
    private var testJob: Job? = null

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


            row {
                cell(testConnectionButton.apply {
                    addActionListener {
                        onTestConnection()
                    }
                })
                cell(testResultField).align(AlignY.CENTER)
            }
            row {
                text("Don't forget to APPLY change after test!")
            }
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


    private fun onTestConnection() {
        val project = ProjectManager.getInstance().openProjects.firstOrNull() ?: return
        // cancel last test job
        testJob?.cancel()
        testResultField.text = "user: hi. robot: "

        // use CoroutineExceptionHandler to handle exception, it will automatically ignore CancelledException
        testJob = ShireCoroutineScope.scope(project).launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            testResultField.text = throwable.message ?: "Unknown error"
        }) {
            val flowString: Flow<String> =
                LlmProvider.provider(project)
                    ?.stream(
                        promptText = "hi",
                        systemPrompt = "",
                        keepHistory = false,
                        llmConfig = LlmConfig(
                            model = modelName.text,
                            apiKey = engineToken.text,
                            apiBase = apiHost.text,
                            temperature = temperatureField.text.toDoubleOrNull() ?: 0.0,
                            title = modelName.text,
                        )
                    )
                    ?: throw Exception(ShireCoreBundle.message("shire.llm.notfound"))
            flowString.collect {
                testResultField.text += it
            }

        }
    }
}
