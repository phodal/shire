package com.phodal.shirelang.provider

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

enum class ShireProvideType(val type: String) {
    Variable("variable"),
    Function("function"),
    Process("process")
    ;

    companion object {
        fun fromString(value: String): ShireProvideType? {
            return entries.firstOrNull { it.type == value }
        }
    }
}

enum class ShireToolchainFunction(val funName: String) {
    /**
     * The provider function offers, Built-in functions in Shire, Built-in variables in Shire, Built-in processes in Shire
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
        val gitFunc = ShireToolchainFunction.fromString(funcName)
            ?: throw IllegalArgumentException("Shire[Toolchain]: Invalid Toolchain function name")

        return when (gitFunc) {
            ShireToolchainFunction.Provider -> {
                val type = args.first() as String
                when (ShireProvideType.fromString(type)) {
                    ShireProvideType.Variable -> {
                        return allVariables
                    }

                    ShireProvideType.Function -> {
                        return emptyList<Any>()
                    }

                    ShireProvideType.Process -> {
                        return emptyList<Any>()
                    }

                    null -> TODO()
                }
            }
        }
    }
}
