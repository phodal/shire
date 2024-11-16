package com.phodal.shirelang

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import org.jetbrains.annotations.NotNull
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Comment injector for Shire language
 */
class ShireInCommentInjector : MultiHostInjector {
    private val SHIRE_CODE_PATTERN: Pattern = Pattern.compile("```shire\\s*(.*?)\\s*```", Pattern.DOTALL)

    override fun getLanguagesToInject(@NotNull registrar: MultiHostRegistrar, @NotNull host: PsiElement) {
        if (host !is PsiDocCommentBase) {
            return
        }

        val commentText = host.getText()
        val matcher: Matcher = SHIRE_CODE_PATTERN.matcher(commentText)

        if (host as? PsiLanguageInjectionHost == null) {
            return
        }

        /// refs to : https://github.com/intellij-rust/intellij-rust/blob/c6657c02bb62075bf7b7ceb84d000f93dda34dc1/src/main/kotlin/org/rust/ide/injected/RsDoctestLanguageInjector.kt
//        while (matcher.find()) {
//            val start: Int = matcher.start(1)
//            val end: Int = matcher.end(1)
//
//            registrar.startInjecting(ShireLanguage.INSTANCE)
//                .addPlace(null, null, host as PsiLanguageInjectionHost, TextRange.create(start, end))
//                .doneInjecting()
//        }
    }

    @NotNull
    override fun elementsToInjectIn(): List<Class<out PsiElement>?> {
        return listOf(PsiComment::class.java)
    }

    companion object {
    }
}
