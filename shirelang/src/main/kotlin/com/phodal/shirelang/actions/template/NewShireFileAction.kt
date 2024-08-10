package com.phodal.shirelang.actions.template

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.NonEmptyInputValidator
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.ShireIcons

class NewShireFileAction : CreateFileFromTemplateAction(
    ShireBundle.message("shire.newFile"), "Creates New Shire Action", ShireIcons.DEFAULT
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

    override fun createFile(name: String?, templateName: String?, dir: PsiDirectory?): PsiFile? {
        val template = FileTemplateManager.getInstance(dir!!.project).getInternalTemplate(templateName!!)
        val newName = name!!.lowercase().replace(" ", "_")

        template.text = template.text.replace("{{name}}", name)
        return createFileFromTemplate(newName, template, dir)
    }
}
