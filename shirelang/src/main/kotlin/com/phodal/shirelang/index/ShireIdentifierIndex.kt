package com.phodal.shirelang.index

import com.intellij.psi.PsiElement
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.phodal.shirelang.ShireFileType
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.psi.ShireFrontMatterKey
import com.phodal.shirelang.psi.ShireVisitor
import java.io.DataInput
import java.io.DataOutput

internal val SHIRE_CONFIG_IDENTIFIER_INDEX_ID = ID.create<String, Int>("shire.index.name")

internal val isIndexing = ThreadLocal<Boolean>()

class ShireIdentifierIndex: FileBasedIndexExtension<String, Int>() {
    override fun getValueExternalizer() = object : DataExternalizer<Int> {
        override fun save(out: DataOutput, value: Int) = out.writeInt(value)
        override fun read(`in`: DataInput) = `in`.readInt()
    }

    override fun getIndexer() = DataIndexer<String, Int, FileContent> {
        val result = mutableMapOf<String, Int>()
        val visitor = object : ShireVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is ShireFrontMatterKey && element.text == HobbitHole.CONFIG_ID) {
                    result[element.text] = element.textOffset
                }

                super.visitElement(element)
            }
        }

        isIndexing.set(true)
        it.psiFile.accept(visitor)
        isIndexing.set(false)
        result
    }

    override fun getName() = SHIRE_CONFIG_IDENTIFIER_INDEX_ID
    override fun getVersion() = 1
    override fun dependsOnFileContent() = true
    override fun getInputFilter() = inputFilter
    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    private val inputFilter = DefaultFileTypeSpecificInputFilter(ShireFileType.INSTANCE)
}
