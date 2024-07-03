package com.phodal.shirelang.completion.dataprovider

import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.VcsToolchainVariable

data class VariableDisplay(
    val name: String,
    val description: String,
    val priority: Double = 0.0
)

object CompositeVariableProvider {
    fun all(): List<VariableDisplay> {
        val results = mutableListOf<VariableDisplay>()

        ContextVariable.values().forEach {
            results.add(VariableDisplay(it.variable, it.description, 99.0))
        }

        PsiContextVariable.values().forEach {
            results.add(VariableDisplay(it.variableName, it.description ?: "", 90.0))
        }

        VcsToolchainVariable.values().forEach {
            results.add(VariableDisplay(it.variableName, it.description, 80.0))
        }

        return results
    }
}