package com.phodal.shire.settings.components

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Panel
import com.phodal.shire.llm.LlmProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.swing.JLabel

fun Panel.testLLMConnection(project: Project?) {
    row {
        val result = JLabel("")
        button("Test LLM Connection") {
            if (project == null) return@button
            result.text = ""

            LlmCoroutineScope.scope(project).launch {
                try {
                    val flowString: Flow<String> =
                        LlmProvider.provider(project)?.stream("hi", "", false)
                            ?: throw IllegalStateException("No LLM provider found")
                    flowString.collect {
                        result.text += it
                    }
                } catch (e: Exception) {
                    result.text = e.message ?: "Unknown error"
                }
            }
        }

        cell(result).align(Align.FILL)
    }
}