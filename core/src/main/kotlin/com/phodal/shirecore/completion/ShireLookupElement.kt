package com.phodal.shirecore.completion

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.openapi.util.ClassConditionKey
import com.intellij.openapi.vfs.VirtualFile

class ShireLookupElement<T : LookupElement> private constructor(
    delegate: T,
    val priority: Double,
    val virtualFile: VirtualFile,
) : LookupElementDecorator<T>(delegate) {
    override fun toString(): String {
        return "ShireLookupElement{priority=$priority, delegate=$delegate}"
    }

    fun getFile(): VirtualFile {
        return virtualFile
    }

    companion object {
        private val CLASS_CONDITION_KEY: ClassConditionKey<ShireLookupElement<*>> = ClassConditionKey.create(
            ShireLookupElement::class.java
        )

        /**
         * @param element element
         * @param priority priority (higher priority puts the item closer to the beginning of the list)
         * @return decorated lookup element
         */
        fun withPriority(element: LookupElement, priority: Double, virtualFile: VirtualFile): LookupElement {
            val prioritized = element.`as`(CLASS_CONDITION_KEY)
            val finalElement = if (prioritized !== element) element else prioritized.delegate
            return ShireLookupElement(finalElement, priority, virtualFile)
        }
    }
}