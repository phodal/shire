package com.phodal.shirelang.proto.provider

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.protobuf.lang.psi.PbMessageTypeName
import com.intellij.protobuf.lang.psi.PbNamedElement
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.resolve.ProtoSymbolPathReference
import com.intellij.protobuf.lang.stub.index.QualifiedNameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex

object ShireProtoUtils {
    fun findCallees(psiElement: PbServiceDefinition, project: Project): List<String> {
        val methods = psiElement.body?.serviceMethodList ?: return listOf()

        val type = methods.map { method ->
            method.serviceMethodTypeList.mapNotNull { type ->
                val messageTypeName: PbMessageTypeName = type.messageTypeName
                val protoSymbolPathReference = messageTypeName.symbolPath.reference as ProtoSymbolPathReference
                protoSymbolPathReference.multiResolve(true).mapNotNull { it.element }
            }.flatten()
        }.flatten()

        return type.distinct().mapNotNull { it.text }
    }

    /**
     * Maybe can use for GoTo Languages?
     */
    private fun getItemsByName(
        project: Project,
        name: String,
    ): List<@NlsSafe String> {
        val projectScope = GlobalSearchScope.projectScope(project)
        val results: MutableCollection<PbNamedElement> = StubIndex.getElements(
            QualifiedNameIndex.KEY, name, project, projectScope,
            PbNamedElement::class.java
        )

        return results.mapNotNull { it.text }
    }
}
