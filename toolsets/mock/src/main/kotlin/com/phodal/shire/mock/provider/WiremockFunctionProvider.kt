package com.phodal.shire.mock.provider

import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirecore.runner.ConfigurationRunner


class WiremockFunctionProvider : ToolchainFunctionProvider, ConfigurationRunner {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return WiremockFunction.values().any { it.funName == funcName }
    }

    override fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any {
        val wiremockFunction = WiremockFunction.fromString(funcName)
            ?: throw IllegalArgumentException("Shire[Wiremock]: Invalid Wiremock function name")

        return when (wiremockFunction) {
            WiremockFunction.Mock -> {
                if (args.isEmpty()) {
                    return "ShireError[Wiremock]: No args found"
                }

                val mockFilepath = args.first()
                val mockFile = project.baseDir.findFileByRelativePath(mockFilepath.toString())
                    ?: throw IllegalArgumentException("ShireError[Wiremock]: No file found: $mockFilepath")

                val jsonFile = runReadAction {
                    PsiManager.getInstance(project).findFile(mockFile)
                } ?: throw IllegalArgumentException("ShireError[Wiremock]: No JsonFile found: $mockFilepath")

                runMock(project, jsonFile)
            }
        }
    }

    private fun runMock(project: Project, configFile: PsiFile): Any {
        val configurationSettings = runReadAction {
            ConfigurationContext(configFile).configurationsFromContext?.firstOrNull()?.configurationSettings
        } ?: throw IllegalArgumentException("ShireError[Wiremock]: Please install Wiremock plugin")

        if (!configurationSettings.name.startsWith("WireMock")) {
            throw IllegalArgumentException("ShireError[Wiremock]: No a valid WireMock configure found")
        }

        val runManager = RunManager.getInstance(project)
        // java.lang.Throwable: WireMock.WireMock mock_v0-stubs.json must be added before selecting
        //	at com.intellij.openapi.diagnostic.Logger.error(Logger.java:376)
        //	at com.intellij.execution.impl.RunManagerImpl.setSelectedConfiguration(Run
        runManager.addConfiguration(configurationSettings)
        runManager.selectedConfiguration = configurationSettings

        configurationSettings.isActivateToolWindowBeforeRun = true
        configurationSettings.isFocusToolWindowBeforeRun = true
        configurationSettings.isTemporary = true

        val runContext = createRunContext()
        executeRunConfigurations(null, configurationSettings, runContext, null, null)

        return "Done"
    }
}
