package com.phodal.shirecore.provider.function

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project

interface ToolchainFunctionProvider {
    fun isApplicable(project: Project, funcName: String): Boolean

    fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any

    companion object {
        private val EP_NAME: ExtensionPointName<ToolchainFunctionProvider> =
            ExtensionPointName("com.phodal.shireToolchainFunctionProvider")

        fun all(): List<ToolchainFunctionProvider> {
            return EP_NAME.extensionList
        }

        fun provide(project: Project, funcName: String): ToolchainFunctionProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isApplicable(project, funcName)
            }
        }
    }
}
