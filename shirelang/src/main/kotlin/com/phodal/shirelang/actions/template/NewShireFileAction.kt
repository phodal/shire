package com.phodal.shirelang.actions.template

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.NonEmptyInputValidator
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.ShireIcons
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.psi.ShireFrontMatterHeader
import com.phodal.shirelang.psi.ShireTypes

class NewShireFileAction : CreateFileFromTemplateAction(
    ShireBundle.message("shire.newFile"), "Creates new AutoDev customize", ShireIcons.DEFAULT
), DumbAware {
    override fun getDefaultTemplateProperty(): String = "DefaultShireTemplate"

    override fun getActionName(psi: PsiDirectory?, p1: String, p2: String?): String =
        ShireBundle.message("shire.newFile")

    override fun buildDialog(project: Project, psiDir: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle(ShireBundle.message("shire.newFile"))
            .addKind(ShireBundle.message("shire.file"), ShireIcons.DEFAULT, "Shire Action")
            .setValidator(NonEmptyInputValidator())
    }

    override fun postProcess(createdElement: PsiFile, templateName: String?, customProperties: Map<String, String>?) {
        super.postProcess(createdElement, templateName, customProperties)

        if (createdElement is ShireFile) {
            val matterHeader =
                PsiTreeUtil.getChildrenOfTypeAsList(createdElement, ShireFrontMatterHeader::class.java).firstOrNull()
                    ?: return

            val editor = FileEditorManager.getInstance(createdElement.project).selectedTextEditor ?: return

            matterHeader.children.forEach { entry ->
                entry.children.forEach { child ->
                    when (child.elementType) {
                        ShireTypes.FRONT_MATTER_KEY -> {
                            if (child.text == "name") {
                                // move to name
                            }
                            if (child.text == "description") {
                                val element = child.nextSibling
                                editor.caretModel.moveToOffset(element.getTextRange().getEndOffset())
                            }
                        }
                    }
                }
            }

        }
    }
}
