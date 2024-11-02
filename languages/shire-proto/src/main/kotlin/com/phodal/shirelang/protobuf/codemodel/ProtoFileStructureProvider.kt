package com.phodal.shirelang.protobuf.codemodel

import com.intellij.protobuf.lang.psi.PbImportStatement
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbPackageStatement
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.codemodel.FileStructureProvider
import com.phodal.shirecore.provider.codemodel.model.FileStructure

class ProtoFileStructureProvider : FileStructureProvider {
    override fun build(psiFile: PsiFile): FileStructure? {
        val name = psiFile.name
        val path = psiFile.virtualFile?.path ?: ""

        val packageName =
            PsiTreeUtil.getChildrenOfTypeAsList(psiFile, PbPackageStatement::class.java).firstOrNull()?.text ?: ""
        val imports = PsiTreeUtil.getChildrenOfTypeAsList(psiFile, PbImportStatement::class.java)

        val messages: List<PbMessageDefinition> =
            PsiTreeUtil.getChildrenOfTypeAsList(psiFile, PbMessageDefinition::class.java)
        val services = PsiTreeUtil.getChildrenOfTypeAsList(psiFile, PbServiceDefinition::class.java)
        val enum = PsiTreeUtil.getChildrenOfTypeAsList(psiFile, PbMessageDefinition::class.java)

        val classes = messages + services + enum

        return FileStructure(psiFile, name, path, packageName, imports, classes, emptyList())
    }
}

