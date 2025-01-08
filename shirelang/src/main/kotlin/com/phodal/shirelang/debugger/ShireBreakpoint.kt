package com.phodal.shirelang.debugger

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import com.phodal.shirelang.ShireFileType

class ShireBreakpointHandler(val process: ShireDebugProcess) :
    XBreakpointHandler<XLineBreakpoint<ShireBpProperties>>(ShireLineBreakpointType::class.java) {
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {
        process.addBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>, temporary: Boolean) {
        process.removeBreakpoint(breakpoint)
    }
}

class ShireBpProperties : XBreakpointProperties<ShireBpProperties>() {
    override fun getState(): ShireBpProperties = this
    override fun loadState(state: ShireBpProperties) {}
}

class ShireLineBreakpointType : XLineBreakpointType<ShireBpProperties>(ID, TITLE) {
    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean {
        return canPutAt(project, file, line)
    }

    override fun createBreakpointProperties(file: VirtualFile, line: Int): ShireBpProperties = ShireBpProperties()


    fun canPutAt(project: Project, file: VirtualFile, line: Int): Boolean {
        // val shireFile = PsiManager.getInstance(project).findFile(file) as? ShireFile ?: return false
        //        val findLeafElementAt = shireFile.node.findLeafElementAt(line)?.elementType
        //        findLeafElementAt?.let {
        //            return true
        //        }
        //
        //        return false
        return (FileTypeRegistry.getInstance().isFileOfType(file, ShireFileType.INSTANCE))
    }

    companion object {
        private const val ID = "the-shire-line"
        private const val TITLE = "Shire Breakpoints"
    }
}