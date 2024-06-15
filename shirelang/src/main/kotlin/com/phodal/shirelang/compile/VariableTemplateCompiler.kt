package com.phodal.shirelang.compile

import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirelang.completion.dataprovider.ContextVariable
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
        variableMap[key] = value
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

        // for compatibility with older versions of AutoDev
        val context = VelocityContext(variableMap)
        val sw = StringWriter()
        try {
            // for compatibility with older versions of AutoDev
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

    companion object {
        /**
         * This function returns the default editor for the given project.
         * It takes a Project object as a parameter and returns an Editor object.
         * It uses the FileEditorManager to get the selected text editor for the project.
         * If no editor is selected, it returns null.
         */
        fun defaultEditor(myProject: Project): Editor? {
            return FileEditorManager.getInstance(myProject).selectedTextEditor
        }

        /**
         * This function returns the PsiElement at the current caret position in the editor.
         *
         * @param myProject the project to which the editor belongs
         * @param currentEditor the current editor where the caret position is located
         * @return the PsiElement at the current caret position, or null if not found
         */
        fun defaultElement(myProject: Project, currentEditor: Editor?): PsiElement? {
            return currentEditor?.caretModel?.currentCaret?.offset?.let {
                val psiFile = currentEditor.let { editor ->
                    val psiFile = editor.virtualFile?.let { file ->
                        PsiManager.getInstance(myProject).findFile(file)
                    }

                    psiFile
                } ?: return@let null

                psiFile.findElementAt(it) ?: return@let psiFile
            }
        }
    }
}