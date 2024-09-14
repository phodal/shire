package com.phodal.shire.wiremock.provider

import com.intellij.execution.RunManager
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.json.psi.JsonFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirecore.runner.ConfigurationRunner

enum class WiremockFunction(val funName: String) {
    Mock("mock")
    ;

    companion object {
        fun fromString(value: String): WiremockFunction? {
            return values().firstOrNull { it.funName == value }
        }
    }
}


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
                    ?: throw IllegalArgumentException("ShireError[Wiremock]: No file found")

                val jsonFile = PsiManager.getInstance(project).findFile(mockFile) as? JsonFile
                    ?: throw IllegalArgumentException("ShireError[Wiremock]: No file found")

                runMock(project, jsonFile)
            }
        }
    }

    private fun runMock(project: Project, mockFile: JsonFile): Any {
        val configurationSettings =
            ConfigurationContext(mockFile).configurationsFromContext?.firstOrNull()?.configurationSettings
                ?: return "Please install Wiremock plugin"

        val runManager = RunManager.getInstance(project)
//        val configure = runManager.allConfigurationsList.firstOrNull() ?: return "Please install Wiremock plugin"
        runManager.selectedConfiguration = configurationSettings

        configurationSettings.isActivateToolWindowBeforeRun = true

        val runContext = createRunContext()
        executeRunConfigures(project, configurationSettings, runContext, null, null)

        return "ShireError[Wiremock]: No file found"
    }
}
