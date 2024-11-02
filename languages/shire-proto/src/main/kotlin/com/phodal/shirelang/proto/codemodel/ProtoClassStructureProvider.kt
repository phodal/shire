package com.phodal.shirelang.proto.codemodel

import com.intellij.protobuf.lang.psi.PbDefinition
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.model.ClassStructure

class ProtoClassStructureProvider : ClassStructureProvider {
    override fun build(psiElement: PsiElement, gatherUsages: Boolean): ClassStructure? {
        if (psiElement !is PbDefinition) return null

        return when (psiElement) {
            is PbMessageDefinition -> {
                val text = psiElement.text
                val name = psiElement.name

                ClassStructure(
                    psiElement,
                    text,
                    name,
                    name,
                    emptyList(),
                    emptyList(),
                    null,
                    emptyList(),
                    emptyList()
                )
            }

            is PbServiceDefinition -> {
                val text = psiElement.text
                val name = psiElement.name

                ClassStructure(
                    psiElement,
                    text,
                    name,
                    name,
                    emptyList(),
                    emptyList(),
                    null,
                    emptyList(),
                    emptyList()
                )
            }

            else -> {
                return null
            }
        }
    }
}
