package com.phodal.shirelang.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.middleware.PostProcessor
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider
import com.phodal.shirelang.compiler.patternaction.PatternActionFunc
import com.phodal.shirelang.compiler.variable.CompositeVariableProvider

enum class ShireProvideType(val type: String) {
    Variables("variables"),
    Functions("functions"),
    Processor("processors")
    ;

    companion object {
        fun fromString(value: String): ShireProvideType? {
            return entries.firstOrNull { it.type == value }
        }
    }
}

enum class ShireToolchainFunction(val funName: String) {
    /**
     * The provider function offers, Built-in functions in Shire, Built-in variables in Shire, lifecycle in Shire
     * for example: `provider("variable")` will return all variables in tables
     */
    Provider("provider");

    companion object {
        fun fromString(value: String): ShireToolchainFunction? {
            return entries.firstOrNull { it.funName == value }
        }
    }
}

class ShireToolchainFunctionProvider : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return ShireToolchainFunction.entries.any { it.funName == funcName }
    }

    override fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any {
        val shireFunc = ShireToolchainFunction.fromString(funcName)
            ?: throw IllegalArgumentException("Shire[Toolchain]: Invalid Toolchain function name")

        when (shireFunc) {
            ShireToolchainFunction.Provider -> {
                val type = args.first() as String
                return when (ShireProvideType.fromString(type)) {
                    ShireProvideType.Variables -> {
                        /// name and description to markdown table
                        var result = "| Name | Description |"
                        CompositeVariableProvider.all().forEach {
                            result += "\n| ${it.name} | ${it.description} |"
                        }

                        result
                    }

                    ShireProvideType.Functions -> {
                        /// funcName and example to markdown table
                        var result = "| Function | Description |"
                        PatternActionFunc.all().forEach {
                            result += "\n| ${it.funcName} | ${it.description} |"
                        }

                        result
                    }

                    ShireProvideType.Processor -> {
                        PostProcessor.all().map {
                            it.processorName
                        }
                    }

                    null -> ""
                }
            }
        }
    }
}
