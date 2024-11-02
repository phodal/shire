package com.phodal.shirelang.proto.provider

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbMessageTypeName
import com.intellij.protobuf.lang.psi.PbNamedElement
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.resolve.ProtoSymbolPathReference
import com.intellij.protobuf.lang.stub.index.QualifiedNameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex

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

        /// lookup messages and resolve typed
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
    ): List<@NlsSafe String> {
        val projectScope = GlobalSearchScope.projectScope(project)
        val results: MutableCollection<PbNamedElement> = StubIndex.getElements(
            QualifiedNameIndex.KEY, name, project, projectScope,
            PbNamedElement::class.java
        )

        return results.mapNotNull { it.text }
    }
}
