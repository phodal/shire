package com.phodal.shirelang.completion.dataprovider

import com.phodal.shirecore.provider.variable.PsiContextVariable

data class VariableDisplay(
    val name: String,
    val description: String,
)

object CompositeVariableProvider {
    fun all(): List<VariableDisplay> {
        val results = mutableListOf<VariableDisplay>()

        ContextVariable.values().forEach {
            results.add(VariableDisplay(it.variable, it.description))
        }

        PsiContextVariable.values().forEach {
            results.add(VariableDisplay(it.variableName, it.description ?: ""))
        }

        return results
    }
}