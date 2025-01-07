package com.phodal.shirelang.debugger

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import com.phodal.shirelang.ShireFileType

class ShireLineBreakpointType : XLineBreakpointTypeBase(ID, TITLE, ShireDebuggerEditorsProvider()) {
    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean {
        return canPutAt(project, file, line)
    }

    fun canPutAt(project: Project, file: VirtualFile, line: Int): Boolean {
        return (FileTypeRegistry.getInstance().isFileOfType(file, ShireFileType.INSTANCE))
    }


    companion object {
        private const val ID = "the-shire-line"
        private const val TITLE = "Shire Breakpoints"
    }
}
