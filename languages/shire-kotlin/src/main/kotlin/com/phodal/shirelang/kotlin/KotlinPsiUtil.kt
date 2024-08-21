package com.phodal.shirelang.kotlin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.MethodReferencesSearch
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction

object KotlinPsiUtil {
    fun getFunctions(kotlinClass: KtClassOrObject): List<KtFunction> {
        return kotlinClass.getDeclarations().filterIsInstance<KtFunction>()
    }

    fun getClasses(ktFile: KtFile): List<KtClassOrObject> {
        return ktFile.declarations.filterIsInstance<KtClassOrObject>()
    }

    fun signatureString(signatureString: KtNamedFunction): String {
        val bodyBlockExpression = signatureString.bodyBlockExpression
        val startOffsetInParent = if (bodyBlockExpression != null) {
            bodyBlockExpression.startOffsetInParent
        } else {
            val bodyExpression = signatureString.bodyExpression
            bodyExpression?.startOffsetInParent ?: signatureString.textLength
        }

        val text = signatureString.text
        val substring = text.substring(0, startOffsetInParent)
        return substring.replace('\n', ' ').trim()
    }

    fun findUsages(nameIdentifierOwner: PsiNameIdentifierOwner): List<PsiReference> {
        val project = nameIdentifierOwner.project
        val searchScope = GlobalSearchScope.allScope(project) as SearchScope

        return when (nameIdentifierOwner) {
            is PsiMethod -> {
                MethodReferencesSearch.search(nameIdentifierOwner, searchScope, true)
            }

            else -> {
                ReferencesSearch.search((nameIdentifierOwner as PsiElement), searchScope, true)
            }
        }.findAll().map { it as PsiReference }
    }
}