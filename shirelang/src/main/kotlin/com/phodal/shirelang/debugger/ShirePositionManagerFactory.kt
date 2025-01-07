package com.phodal.shirelang.debugger

import com.intellij.debugger.*
import com.intellij.debugger.engine.DebugProcess
import com.intellij.debugger.engine.PositionManagerWithMultipleStackFrames
import com.intellij.debugger.engine.evaluation.EvaluationContext
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.debugger.requests.ClassPrepareRequestor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.util.ThreeState
import com.phodal.shirelang.ShireFileType
import com.phodal.shirelang.psi.ShireFile
import com.sun.jdi.Location
import com.sun.jdi.ReferenceType
import com.sun.jdi.request.ClassPrepareRequest

class ShirePositionManagerFactory : PositionManagerFactory() {
    override fun createPositionManager(process: DebugProcess): PositionManager? {
        return ShirePositionManager(process)
    }
}

class ShirePositionManager(private val debugProcess: DebugProcess) : MultiRequestPositionManager,
    PositionManagerWithMultipleStackFrames {
    override fun getAcceptedFileTypes(): Set<FileType> = setOf(ShireFileType.INSTANCE)
    override fun getSourcePosition(location: Location?): SourcePosition? {
//        return SourcePosition.createFromLine(debugProcess.project, location?.sourcePath, location?.lineNumber)
        return null
    }

    override fun getAllClasses(position: SourcePosition): MutableList<ReferenceType> {
        return mutableListOf()
    }

    override fun locationsOfLine(type: ReferenceType, sourcePosition: SourcePosition): MutableList<Location> {
        return mutableListOf()
    }

    override fun createPrepareRequest(
        classPrepareRequestor: ClassPrepareRequestor,
        sourcePosition: SourcePosition,
    ): ClassPrepareRequest? = createPrepareRequests(classPrepareRequestor, sourcePosition).firstOrNull()

    override fun evaluateCondition(
        context: EvaluationContext,
        frame: StackFrameProxyImpl,
        location: Location,
        expression: String,
    ): ThreeState = ThreeState.UNSURE

    override fun createPrepareRequests(
        requestor: ClassPrepareRequestor, position: SourcePosition,
    ): MutableList<ClassPrepareRequest> {
        val file = position.file
        if (file !is ShireFile) {
            throw NoDataException.INSTANCE
        }

//        val xBreakpoint = requestor.asSafely<Breakpoint<*>>()?.xBreakpoint
//        val xSession = debugProcess.asSafely<DebugProcessImpl>()?.xdebugProcess?.session.asSafely<XDebugSessionImpl>()

        return mutableListOf()
    }
}

