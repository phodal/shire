package com.phodal.shire.wiremock.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

enum class WiremockFunction(val funName: String) {
    Mock("mock")
    ;

    companion object {
        fun fromString(value: String): WiremockFunction? {
            return values().firstOrNull { it.funName == value }
        }
    }
}


class WiremockFunctionProvider : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return WiremockFunction.values().any { it.funName == funcName }
    }

    override fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any {
        val wiremockFunction = WiremockFunction.fromString(funcName)
            ?: throw IllegalArgumentException("Shire[Wiremock]: Invalid Wiremock function name")


        return when (wiremockFunction) {
            WiremockFunction.Mock -> {
//                WireMockRunConfiguration
            }
        }
    }
}
