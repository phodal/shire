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
}
