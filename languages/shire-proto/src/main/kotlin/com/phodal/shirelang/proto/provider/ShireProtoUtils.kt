package com.phodal.shirelang.proto.provider

import com.intellij.openapi.project.Project
import com.intellij.protobuf.ide.gutter.findImplementations
import com.intellij.protobuf.lang.findusages.PbFindUsagesProvider
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbMessageTypeName
import com.intellij.protobuf.lang.psi.PbNamedElement
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.resolve.ProtoSymbolPathReference
import com.intellij.protobuf.lang.stub.index.QualifiedNameIndex
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.FileStructureProvider
import com.phodal.shirecore.provider.codemodel.MethodStructureProvider

object ShireProtoUtils {
    fun lookupDependenceMessages(psiElement: PbServiceDefinition, project: Project): List<PbMessageDefinition> {
        val methods = psiElement.body?.serviceMethodList ?: return listOf()

        val type = methods.map { method ->
            method.serviceMethodTypeList.mapNotNull { type ->
                val messageTypeName: PbMessageTypeName = type.messageTypeName
                val protoSymbolPathReference = messageTypeName.symbolPath.reference as ProtoSymbolPathReference
                protoSymbolPathReference.multiResolve(true).mapNotNull {
                    it.element as? PbMessageDefinition
                }
            }.flatten()
        }.flatten()

        val messages = type.distinct()

        val secondLevels = messages.mapNotNull {
            it.body?.simpleFieldList?.mapNotNull { field ->
                field.typeName.symbolPath.reference?.resolve() as? PbMessageDefinition
            }
        }.flatten().distinct()

        return messages + secondLevels
    }

    /**
     * Maybe can use for GoTo Languages?
     */
    private fun getItemsByName(
        project: Project,
        name: String,
    ): List<PbNamedElement> {
        val projectScope = GlobalSearchScope.projectScope(project)
        val results: MutableCollection<PbNamedElement> = StubIndex.getElements(
            QualifiedNameIndex.KEY, name, project, projectScope,
            PbNamedElement::class.java
        )

        return results.toList()
    }

    fun lookupUsage(psiElement: PsiElement, project: Project): List<String> {
        if (!PbFindUsagesProvider().canFindUsagesFor(psiElement)) {
            return listOf()
        }

        return when (psiElement) {
            is PbMessageDefinition -> {
                return findImplClassCode(psiElement)
            }

            is PbServiceDefinition -> {
                findImplClassCode(psiElement)
            }

            else -> listOf()
        }
    }

    private fun findImplClassCode(psiElement: PsiElement): List<String> {
        return findImplementations(psiElement).map {
            formatElement(it) ?: it.text
        }.toList()
    }

    private fun formatElement(psiElement: PsiElement): String? {
        return when {
            psiElement is PsiFile -> FileStructureProvider.from(psiElement)?.format()
            else -> ClassStructureProvider.from(psiElement, false)?.format()
                ?: MethodStructureProvider.from(psiElement, false)?.format()
        }
    }
}
