package com.phodal.shirelang.debugger

import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile


/**
 * the snapshot of TimeTravel Debugger
 * ```shire
 * ---
 * name: "Context Variable"
 * description: "Here is a description of the action."
 * interaction:  RunPanel
 * variables:
 *   "contextVariable": /ContextVariable\.kt/ { cat }
 *   "psiContextVariable": /PsiContextVariable\.kt/ { cat }
 * onStreamingEnd: { parseCode | saveFile("docs/shire/shire-builtin-variable.md") }
 * ---
 *
 * 根据如下的信息，编写对应的 ContextVariable 相关信息的 markdown 文档。
 * ```
 */
class ShireFileSnapshot(
    val file: VirtualFile,
    val rnd: Int, // seed for random number generator
    val variables: Map<String, ResolvableVariableSnapshot>,
) {
    private val snapshots = mutableListOf<TimeTravelSnapshot>()
    
    fun takeSnapshot() {
        snapshots.add(TimeTravelSnapshot(variables.mapValues { it.value.getCurrentValue() }))
    }
    
    fun replayTo(index: Int) {
        if (index < snapshots.size) {
            snapshots[index].restore(variables)
        }
    }
}


/**
 * Variable Snapshot will store all change flow of a variable. For example:
 * ```shire
 * ---
 * variables:
 *   "controllers": /.*.java/ { cat | grep("class\s+([a-zA-Z]*Controller)")  }
 * ---
 * ```
 *
 * The variable snapshot should store:
 *
 * - the value after cat function
 * - the value after grep function
 */
data class VariableOperation(
    val functionName: String,
    val timestamp: Long,
    val value: Any?
)

class ResolvableVariableSnapshot(
    val variableName: String,
    val sourceFiles: List<VirtualFile> = mutableListOf(),
    val funcName: String, // todo design for function type
    /// todo
) : UserDataHolderBase() {
    private val valueHistory = mutableListOf<Any>()
    private var currentValue: Any? = null
    private val operations = mutableListOf<VariableOperation>()
    
    fun recordValue(value: Any) {
        currentValue = value
        valueHistory.add(value)
        operations.add(VariableOperation(funcName, System.currentTimeMillis(), value))
    }
    
    fun getCurrentValue(): Any? = currentValue
    
    fun getHistory(): List<Any> = valueHistory.toList()
    
    fun getOperations(): List<VariableOperation> = operations.toList()
}

class ResolvableVariableRecord(
    val variableName: String,
    val executedFunction: String,
    val timestamp: Long,
    val value: Any
)

class ResolvableVariableReplay(
    private val records: List<ResolvableVariableRecord>
) {
    fun replayAt(timestamp: Long): Map<String, Any> {
        return records
            .filter { it.timestamp <= timestamp }
            .groupBy { it.variableName }
            .mapValues { it.value.last().value }
    }
}

data class TimeTravelSnapshot(
    val variableStates: Map<String, Any?>
) {
    fun restore(variables: Map<String, ResolvableVariableSnapshot>) {
        variableStates.forEach { (name, value) ->
            variables[name]?.recordValue(value ?: "")
        }
    }
}
