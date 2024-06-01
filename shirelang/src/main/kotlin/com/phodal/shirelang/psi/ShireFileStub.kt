package com.phodal.shirelang.psi

import com.intellij.psi.PsiFile
import com.intellij.psi.StubBuilder
import com.intellij.psi.stubs.*
import com.intellij.psi.tree.IStubFileElementType
import com.phodal.shirelang.ShireLanguage

class ShireFileStub(file: ShireFile?, private val flags: Int) : PsiFileStubImpl<ShireFile>(file) {
    override fun getType() = Type

    object Type : IStubFileElementType<ShireFileStub>(ShireLanguage) {
        override fun getStubVersion(): Int = 1

        override fun getExternalId(): String = "devin.file"

        override fun serialize(stub: ShireFileStub, dataStream: StubOutputStream) {
            dataStream.writeByte(stub.flags)
        }

        override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): ShireFileStub {
            return ShireFileStub(null, dataStream.readUnsignedByte())
        }

        override fun getBuilder(): StubBuilder = object : DefaultStubBuilder() {
            override fun createStubForFile(file: PsiFile): StubElement<*> {
                return ShireFileStub(file as ShireFile, 0)
            }
        }
    }
}