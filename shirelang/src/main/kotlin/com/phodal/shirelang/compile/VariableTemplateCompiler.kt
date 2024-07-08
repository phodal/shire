package com.phodal.shirelang.compile

import com.intellij.lang.Language
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.StringWriter

class VariableTemplateCompiler(
    val language: Language,
    val file: PsiFile,
) {
    private val log = logger<VariableTemplateCompiler>()
    private val variableMap: MutableMap<String, Any> = mutableMapOf()

    fun putAll(map: Map<String, Any>) {
        variableMap.putAll(map)
    }

    fun compile(template: String): String {
        val oldContextClassLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = VariableTemplateCompiler::class.java.classLoader

        // for compatibility with older versions of AutoDev
        val context = VelocityContext(variableMap as Map<String, Any>?)
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
        fun defaultElement(myProject: Project, currentEditor: Editor?): PsiElement? =
            ReadAction.compute<PsiElement?, Throwable> {
                currentEditor?.caretModel?.currentCaret?.offset?.let {
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