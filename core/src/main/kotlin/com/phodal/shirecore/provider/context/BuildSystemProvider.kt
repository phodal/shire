package com.phodal.shirecore.provider.context


import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.serviceContainer.LazyExtensionInstance
import com.intellij.util.xmlb.annotations.Attribute
import com.phodal.shirecore.toolchain.buildsystem.BuildSystemContext

/**
 * The `BuildSystemProvider` interface represents a provider for build system information.
 * It provides methods to retrieve the name and version of the build tool being used, as well as the name
 * and version of the programming language being used.
 */
abstract class BuildSystemProvider : LazyExtensionInstance<BuildSystemProvider>() {
    abstract fun collect(project: Project): BuildSystemContext?

    @Attribute("implementationClass")
    var implementationClass: String? = null

    override fun getImplementationClassName(): String? {
        return implementationClass
    }

    companion object {
        private val EP_NAME: ExtensionPointName<BuildSystemProvider> =
            ExtensionPointName.create("com.phodal.shireBuildSystemProvider")

        fun provide(project: Project): List<BuildSystemContext> {
            return EP_NAME.extensionList.mapNotNull {
                it.collect(project)
            }
        }
    }
}