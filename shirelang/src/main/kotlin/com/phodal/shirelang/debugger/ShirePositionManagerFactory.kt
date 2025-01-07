package com.phodal.shirelang.debugger

import com.intellij.debugger.MultiRequestPositionManager
import com.intellij.debugger.PositionManager
import com.intellij.debugger.PositionManagerFactory
import com.intellij.debugger.SourcePosition
import com.intellij.debugger.engine.DebugProcess
import com.intellij.debugger.engine.PositionManagerWithMultipleStackFrames
import com.intellij.debugger.engine.evaluation.EvaluationContext
import com.intellij.debugger.jdi.StackFrameProxyImpl
import com.intellij.debugger.requests.ClassPrepareRequestor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.util.ThreeState
import com.sun.jdi.Location
import com.sun.jdi.ReferenceType
import com.sun.jdi.request.ClassPrepareRequest
import com.phodal.shirelang.ShireFileType

class ShirePositionManagerFactory : PositionManagerFactory() {
    override fun createPositionManager(process: DebugProcess): PositionManager? {
        return ShirePositionManager(process)
    }
}

class ShirePositionManager(private val process: DebugProcess) : MultiRequestPositionManager,
    PositionManagerWithMultipleStackFrames {
    override fun getAcceptedFileTypes(): Set<FileType> = setOf(ShireFileType.INSTANCE)
    override fun getSourcePosition(location: Location?): SourcePosition? {
        return null
    }

    override fun getAllClasses(position: SourcePosition): MutableList<ReferenceType> {
        return mutableListOf()
    }

    override fun locationsOfLine(type: ReferenceType, sourcePosition: SourcePosition): MutableList<Location> {
        return mutableListOf()
    }

    override fun createPrepareRequest(
        requestor: ClassPrepareRequestor,
        sourcePosition: SourcePosition,
    ): ClassPrepareRequest? {
        return null
    }

    override fun evaluateCondition(
        context: EvaluationContext,
        frame: StackFrameProxyImpl,
        location: Location,
        expression: String,
    ): ThreeState {
        return ThreeState.UNSURE
    }

    override fun createPrepareRequests(
        requestor: ClassPrepareRequestor, position: SourcePosition,
    ): MutableList<ClassPrepareRequest> {
//        val file = position.file
        return mutableListOf()
    }
}

