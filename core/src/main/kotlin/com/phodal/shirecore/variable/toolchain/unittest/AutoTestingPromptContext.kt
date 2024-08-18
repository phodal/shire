package com.phodal.shirecore.variable.toolchain.unittest

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.phodal.shirecore.context.template.TemplateContext

data class AutoTestingPromptContext(
    val isNewFile: Boolean,
    val outputFile: VirtualFile,
    val relatedClasses: List<String> = emptyList(),
    val testClassName: String?,
    val language: Language,
    /**
     * In Java, it is the current class.
     * In Kotlin, it is the current class or current function.
     * In JavaScript, it is the current class or current function.
     */
    val currentObject: String? = null,
    val imports: List<String> = emptyList(),
    /** Since 1.5.4, since some languages have different test code insertion strategies,
     * we need to pass in the test element text
     */
    val testElement: PsiElement? = null,
) : TemplateContext