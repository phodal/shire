package com.phodal.shirelang.run

import com.phodal.shirelang.ShireIcons
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.phodal.shirelang.ShireBundle
import org.jdom.Element
import javax.swing.Icon

class ShireConfiguration(project: Project, factory: ConfigurationFactory, name: String) :
    LocatableConfigurationBase<ConfigurationFactory>(project, factory, name) {
    override fun getIcon(): Icon = ShireIcons.DEFAULT

    private var myScriptPath = ""
    private val SCRIPT_PATH_TAG: String = "SCRIPT_PATH"

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return ShireRunConfigurationProfileState(project, this)
    }

    override fun checkConfiguration() {
        if (!FileUtil.exists(myScriptPath)) {
            throw RuntimeConfigurationError(ShireBundle.message("shire.run.error.script.not.found"))
        }
    }

    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        element.writeString(SCRIPT_PATH_TAG, myScriptPath)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        myScriptPath = element.readString(SCRIPT_PATH_TAG) ?: ""
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> = ShireSettingsEditor(project)

    fun getScriptPath(): String = myScriptPath

    fun setScriptPath(scriptPath: String) {
        myScriptPath = scriptPath.trim { it <= ' ' }
    }
}

fun Element.writeString(name: String, value: String) {
    val opt = Element("option")
    opt.setAttribute("name", name)
    opt.setAttribute("value", value)
    addContent(opt)
}

fun Element.readString(name: String): String? =
    children
        .find { it.name == "option" && it.getAttributeValue("name") == name }
        ?.getAttributeValue("value")
