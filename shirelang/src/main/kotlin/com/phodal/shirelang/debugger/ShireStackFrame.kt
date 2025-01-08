package com.phodal.shirelang.debugger

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.frame.XSuspendContext

class ShireStackFrame(
    val process: ShireDebugProcess,
) : XStackFrame(), Disposable {
    override fun customizePresentation(component: ColoredTextContainer) {
        // todo
        component.append("Variables", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        component.setIcon(AllIcons.Debugger.Frame)
    }

    override fun dispose() {

    }
}

class ShireDebugEvaluator : XDebuggerEvaluator() {
    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        //// todo
    }
}

class ShireSuspendContext(val process: ShireDebugProcess): XSuspendContext() {
    private val executionStacks: Array<XExecutionStack> = arrayOf(
        ExecutionStack(process)
    )

    override fun getActiveExecutionStack(): XExecutionStack? = executionStacks.firstOrNull()
    override fun getExecutionStacks(): Array<XExecutionStack> = executionStacks
}

class ExecutionStack(private val process: ShireDebugProcess) :
    XExecutionStack("Custom variables") {
    private val stackFrames: List<ShireStackFrame> = listOf(
        ShireStackFrame(process)
    )

    override fun getTopFrame(): XStackFrame? = stackFrames.firstOrNull()

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer) {
        container.addStackFrames(stackFrames, true)
    }
}
