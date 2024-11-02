package com.phodal.shirelang.proto.provider

import com.intellij.protobuf.lang.psi.PbFile
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.resolve.PbSymbolResolver

object ShireProtoUtils {
    fun findCallees(psiElement: PbServiceDefinition): Any {
        val methods = psiElement.body?.serviceMethodList
        if (methods == null) {
            return ""
        }

        val psiFile: PbFile = psiElement.containingFile as? PbFile ?: return ""
        val symbolResolver = PbSymbolResolver.forFile(psiFile)

//        val type = methods.map {
//            it.serviceMethodTypeList.mapNotNull {
//                val reference = it.reference ?: return@mapNotNull null
////                PbPsiUtil.resolveRefToType(reference)?.text
//
//            }
//        }


        return ""
    }

}
