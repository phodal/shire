package com.phodal.shirelang.compiler.variable

import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirecore.provider.variable.model.toolchain.VcsToolchainVariable
import com.phodal.shirecore.provider.variable.model.SystemInfoVariable
import com.phodal.shirecore.provider.variable.model.toolchain.DatabaseToolchainVariable
import com.phodal.shirelang.compiler.variable.value.ContextVariable

data class VariableDisplay(
    val name: String,
    val description: String,
    val priority: Double = 0.0
)

object CompositeVariableProvider {
    fun all(): List<VariableDisplay> {
        val results = mutableListOf<VariableDisplay>()

        ContextVariable.entries.forEach {
            results.add(VariableDisplay(it.variableName, it.description, 99.0))
        }

        PsiContextVariable.entries.forEach {
            results.add(VariableDisplay(it.variableName, it.description ?: "", 90.0))
        }

        VcsToolchainVariable.entries.forEach {
            results.add(VariableDisplay(it.variableName, it.description, 80.0))
        }

        DatabaseToolchainVariable.entries.forEach {
            results.add(VariableDisplay(it.variableName, it.description, 70.0))
        }

        ToolchainVariable.all().forEach {
            results.add(VariableDisplay(it.variableName, it.description, 70.0))
        }

        SystemInfoVariable.all().forEach {
            results.add(VariableDisplay(it.variableName, it.description, 60.0))
        }

        return results
    }
}