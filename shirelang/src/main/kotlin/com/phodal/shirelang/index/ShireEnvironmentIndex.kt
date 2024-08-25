package com.phodal.shirelang.index

import com.intellij.json.JsonFileType
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import java.io.DataInput
import java.io.DataOutput
import java.util.stream.Collectors

class ShireEnvironmentIndex : FileBasedIndexExtension<String, Set<String>>() {
    private val SHIRE_ENV_ID: ID<String, Set<String>> = ID.create("http.request.execution.environment")

    override fun getName(): ID<String, Set<String>> = SHIRE_ENV_ID

    override fun getIndexer(): DataIndexer<String, Set<String>, FileContent> {
        return DataIndexer { inputData: FileContent ->
            val file = inputData.psiFile
            require(file is JsonFile) { AssertionError() }

            val variablesFromFile = getVariablesFromFile(file)
            variablesFromFile
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> {
        return EnumeratorStringDescriptor.INSTANCE
    }

    override fun getValueExternalizer(): DataExternalizer<Set<String>> {
        return ShireStringsExternalizer()
    }

    override fun getVersion(): Int {
        return 1
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return ShireEnvironmentInputFilter()
    }

    override fun dependsOnFileContent(): Boolean {
        return true
    }

    private fun getVariablesFromFile(file: PsiFile): Map<String, Set<String>> {
        val topLevelValue: JsonObject? = (file as JsonFile).topLevelValue as? JsonObject
        if (topLevelValue !is JsonObject) {
            return emptyMap()
        }

        val result: MutableMap<String, Set<String>> = HashMap()
        for (property in topLevelValue.propertyList) {
            if (property.value is JsonObject) {
                result[property.name] = readEnvVariables(property.value as JsonObject, file.name)
            }
        }

        return result
    }

    private fun readEnvVariables(obj: JsonObject, fileName: String): Set<String> {
        val properties = obj.propertyList
        return if (properties.isEmpty()) {
            emptySet()
        } else {
            val set = properties.stream()
                .map { it.name }
                .filter { it != null }
                .collect(Collectors.toSet())

            set
        }
    }
}

class ShireStringsExternalizer : DataExternalizer<Set<String>> {
    override fun save(out: DataOutput, value: Set<String>) {
        out.writeInt(value.size)
        for (s in value) {
            out.writeUTF(s)
        }
    }

    override fun read(input: DataInput): Set<String> {
        val size = input.readInt()
        val result: MutableSet<String> = HashSet(size)
        for (i in 0 until size) {
            result.add(input.readUTF())
        }

        return result
    }

}


class ShireEnvironmentInputFilter : DefaultFileTypeSpecificInputFilter(*arrayOf<FileType>(JsonFileType.INSTANCE)) {
    override fun acceptInput(file: VirtualFile): Boolean {
        return super.acceptInput(file) && isShireEnvFile(file)
    }

    private fun isShireEnvFile(file: VirtualFile?): Boolean {
        if (file != null) {
            val fileName = file.name
            return fileName.endsWith(".shireEnv.json")
        }

        return false
    }
}