package com.phodal.shirecore.index

import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import java.util.stream.Collectors

val SHIRE_ENV_ID: ID<String, Set<String>> = ID.create("shire.environment")

class ShireEnvironmentIndex : FileBasedIndexExtension<String, Set<String>>() {

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
            when (val value = property.value) {
                is JsonObject -> {
                    result[property.name] = readEnvVariables(value, file.name)
                }
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
                .map { property ->
                    StringUtil.nullize(property.name)
                }
                .toList()
                .mapNotNull { it }
                .toSet()

            set
        }
    }
}
