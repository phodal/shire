package com.phodal.shirelang.debugger

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.testFramework.LightVirtualFile
import com.intellij.xdebugger.XDebuggerUtil
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase
import com.phodal.shirelang.ShireFileType
import com.phodal.shirelang.psi.ShireFile

class ShireDebuggerEditorsProvider: XDebuggerEditorsProviderBase() {
    override fun getFileType(): FileType = ShireFileType.INSTANCE

    override fun createExpressionCodeFragment(project: Project, text: String, context: PsiElement?, isPhysical: Boolean): PsiFile {
        val name = "fragment" + "." + ShireFileType.INSTANCE.defaultExtension
        val viewProvider = PsiManagerEx.getInstanceEx(project).fileManager.createFileViewProvider(
            LightVirtualFile(name, ShireFileType.INSTANCE, text), isPhysical)
        return ShireFile(viewProvider)
    }

    override fun getContextElement(virtualFile: VirtualFile, offset: Int, project: Project): PsiElement? {
        return XDebuggerUtil.getInstance().findContextElement(virtualFile, offset, project, true)
    }
}