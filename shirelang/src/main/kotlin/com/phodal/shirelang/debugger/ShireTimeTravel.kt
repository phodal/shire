package com.phodal.shirelang.debugger

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolver
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


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
    var variables: Map<String, ResolvableVariableSnapshot>,
    var allCode: String = "",
    /**
     * execute to current code line
     */
    var executedCode: String = "",
    val metadata: SnapshotMetadata = SnapshotMetadata(Clock.System.now(), "0.0.1", file),
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

    fun updateVariable(variableName: String, executeFunction: ExecuteFunction, newValue: Any) {
        val variable = variables[variableName] ?: return
        variable.recordValue("new value")
    }
}

data class SnapshotMetadata(
    val createdAt: Instant,          // 创建时间
    val version: String,             // 版本号或其他标识
    val file: VirtualFile           // 文件的虚拟路径
)

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
    val value: Any?,
)

class ResolvableVariableSnapshot(
    val variableName: String,
    val value: Any? = null,
    val className: String? = VariableResolver::class.java.name,
    private val executeFunctions: List<ExecuteFunction> = mutableListOf(),
    private val context: ExecutionContext = ExecutionContext(),
) : UserDataHolderBase() {
    private val valueHistory = mutableListOf<Any>()
    private var currentValue: Any? = null
    private val operations = mutableListOf<VariableOperation>()

    fun recordValue(value: Any, functionIndex: Int = -1) {
        currentValue = value
        valueHistory.add(value)
        val funcName = if (functionIndex >= 0) executeFunctions[functionIndex].name else "initial"
        operations.add(VariableOperation(funcName, System.currentTimeMillis(), value))
    }

    fun getCurrentValue(): Any? = currentValue

    fun getHistory(): List<Any> = valueHistory.toList()

    fun getOperations(): List<VariableOperation> = operations.toList()
}

@Service(Service.Level.PROJECT)
class VariableSnapshotRecorder {
    private val snapshots = mutableListOf<ResolvableVariableSnapshot>()

    fun addSnapshot(variables: Map<String, Any?>, trigger: VariableResolver? = null) {
        variables.forEach { (name, value) ->
            snapshots.add(ResolvableVariableSnapshot(name, value))
        }
    }

    fun clear() {
        snapshots.clear()
    }

    fun printSnapshot() {
        snapshots.forEach { snapshot ->
            println("Variable: ${snapshot.variableName}, Value: ${snapshot.getCurrentValue()}")
            snapshot.getOperations().forEach { operation ->
                println("  - ${operation.functionName}: ${operation.value}")
            }
        }
    }

    companion object {
        fun getInstance(project: Project): VariableSnapshotRecorder {
            return project.getService(VariableSnapshotRecorder::class.java)
        }
    }
}

@Service(Service.Level.PROJECT)
class UserCustomVariableSnapshotRecorder {
    private val snapshots = mutableListOf<ResolvableVariableSnapshot>()

    fun clear() {
        snapshots.clear()
    }

    companion object {
        fun getInstance(project: Project): UserCustomVariableSnapshotRecorder {
            return project.getService(UserCustomVariableSnapshotRecorder::class.java)
        }
    }
}

data class ExecuteFunction(
    val name: String,
    val args: List<String> = emptyList(),
    val returnType: String? = null,
)

data class ExecutionContext(
    val variables: MutableMap<String, Any> = mutableMapOf(),
    val environment: MutableMap<String, String> = mutableMapOf(),
    val metadata: MutableMap<String, Any> = mutableMapOf(),
)

data class TimeTravelSnapshot(val variableStates: Map<String, Any?>) {
    fun restore(variables: Map<String, ResolvableVariableSnapshot>) {
        variableStates.forEach { (name, value) ->
            variables[name]?.recordValue(value ?: "")
        }
    }
}
