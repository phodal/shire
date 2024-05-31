package com.phodal.shirelang.completion.dataprovider

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.phodal.shirelang.ShireIcons
import javax.swing.Icon

data class CustomCommand(
    val commandName: String,
    val content: String,
    val icon: Icon = ShireIcons.COMMAND
) {
    companion object {
        fun all(project: Project): List<CustomCommand> {
            return listOf()
        }

        /**
         *  Read the content from the given file and create a CustomCommand object with the file name and content.
         *  @param file the VirtualFile from which the content will be read
         *  @return CustomCommand object containing the name of the file without extension and the content of the file
         */
        private fun fromFile(file: VirtualFile): CustomCommand {
            val content = file.inputStream.readBytes().toString(Charsets.UTF_8)
            return CustomCommand(file.nameWithoutExtension, content)
        }

        fun fromString(project: Project, agentName: String): CustomCommand? {
            return all(project).find { it.commandName == agentName }
        }
    }
}

