package com.phodal.shirelang.compile

import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirelang.completion.provider.ContextVariable
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.StringWriter

class VariableTemplateCompiler(
    val language: Language,
    val file: PsiFile,
    val element: PsiElement?,
    val editor: Editor,
    selectedText: String = "",
) {
    private val log = logger<VariableTemplateCompiler>()
    private val variableMap: MutableMap<String, Any> = mutableMapOf()

    init {
        this.set(ContextVariable.SELECTION.variable, editor.selectionModel.selectedText ?: selectedText)
        this.set(ContextVariable.BEFORE_CURSOR.variable, file.text.substring(0, editor.caretModel.offset))
        this.set(ContextVariable.AFTER_CURSOR.variable, file.text.substring(editor.caretModel.offset))
        this.set(ContextVariable.ALL.variable, file.text)
    }

    fun set(key: String, value: String) {
        variableMap.put(key, value)
    }

    fun compile(template: String): String {
        variableMap[ContextVariable.FILE_NAME.variable] = file.name
        variableMap[ContextVariable.FILE_PATH.variable] = file.virtualFile?.path ?: ""
        variableMap[ContextVariable.METHOD_NAME.variable] = when (element) {
            is PsiNameIdentifierOwner -> element.nameIdentifier?.text ?: ""
            else -> ""
        }

        configForLanguage()

        val oldContextClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = VariableTemplateCompiler::class.java.classLoader

        val context = VelocityContext()
        val sw = StringWriter()
        try {
            context.put("context", variableMap)
            Velocity.evaluate(context, sw, "#" + this.javaClass.name, template)
        } catch (e: Exception) {
            log.error("Failed to compile template: $template", e)
            sw.write(template)
        }

        Thread.currentThread().contextClassLoader = oldContextClassLoader
        return sw.toString()
    }

    private fun configForLanguage() {
        variableMap[ContextVariable.LANGUAGE.variable] = language.displayName
        variableMap[ContextVariable.COMMENT_SYMBOL.variable] = when (language.displayName.lowercase()) {
            "java", "kotlin" -> "//"
            "python" -> "#"
            "javascript" -> "//"
            "typescript" -> "//"
            "go" -> "//"
            "c", "c++", "c#" -> "//"
            "rust" -> "//"
            "ruby" -> "#"
            "shell" -> "#"
            else -> "-"
        }
    }
}