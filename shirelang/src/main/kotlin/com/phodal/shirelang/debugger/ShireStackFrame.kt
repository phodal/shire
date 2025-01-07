package com.phodal.shirelang.debugger

import com.intellij.openapi.Disposable
import com.intellij.xdebugger.frame.XStackFrame

class ShireStackFrame(
    val functionName: String,
    val shireStackFrame: ShireStackFrame?,
) : XStackFrame(), Disposable {
    override fun dispose() {

    }
}