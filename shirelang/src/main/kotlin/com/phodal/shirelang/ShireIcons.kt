package com.phodal.shirelang

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object ShireIcons {
    @JvmField
    val DEFAULT: Icon = IconLoader.getIcon("/icons/shire.svg", ShireIcons::class.java)
    @JvmField
    val COMMAND: Icon = IconLoader.getIcon("/icons/shire.svg", ShireIcons::class.java)
    @JvmField
    val Terminal: Icon = IconLoader.getIcon("/icons/terminal.svg", ShireIcons::class.java)
    @JvmField
    val Idea: Icon = IconLoader.getIcon("/icons/idea.svg", ShireIcons::class.java)
    @JvmField
    val PsiExpr: Icon = IconLoader.getIcon("/icons/shire-psi-expr.svg", ShireIcons::class.java)
    @JvmField
    val Variable: Icon = IconLoader.getIcon("/icons/shire-variable.svg", ShireIcons::class.java)
    @JvmField
    val Pipeline: Icon = IconLoader.getIcon("/icons/shire-pipeline.svg", ShireIcons::class.java)
    @JvmField
    val Case: Icon = IconLoader.getIcon("/icons/shire-case.svg", ShireIcons::class.java)
}
