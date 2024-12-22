package com.phodal.shirelang.run

import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key

class ShireProcessAdapter(val configuration: ShireConfiguration, val consoleView: ShireConsoleView?) :
    ProcessAdapter() {
    var result = ""
    private var llmOutput: String = ""

    override fun processTerminated(event: ProcessEvent) {
        super.processTerminated(event)

        ApplicationManager.getApplication().messageBus
            .syncPublisher(ShireRunListener.TOPIC)
            .runFinish(result, llmOutput, event, configuration.getScriptPath(), consoleView)
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        super.onTextAvailable(event, outputType)
        result = consoleView?.output().toString()
    }

    fun setLlmOutput(llmOutput: String?) {
        if (llmOutput != null) {
            this.llmOutput = llmOutput
        }
    }
}