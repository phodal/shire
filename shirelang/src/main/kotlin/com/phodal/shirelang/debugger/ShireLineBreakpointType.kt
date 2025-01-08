package com.phodal.shirelang.debugger

import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import com.phodal.shirelang.ShireFileType


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
