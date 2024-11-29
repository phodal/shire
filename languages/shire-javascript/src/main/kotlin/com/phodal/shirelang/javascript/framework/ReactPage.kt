package com.phodal.shirelang.javascript.framework

import com.intellij.lang.javascript.JavaScriptFileType
import com.intellij.lang.javascript.TypeScriptJSXFileType
import com.intellij.lang.javascript.dialects.ECMA6LanguageDialect
import com.intellij.lang.javascript.dialects.TypeScriptJSXLanguageDialect
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.variable.frontend.Component
import com.phodal.shirecore.variable.frontend.ComponentProvider
import kotlinx.serialization.json.Json

enum class RouterFile(val filename: String) {
    UMI(".umirc.ts"),
    NEXT("next.config.js"),
    VITE("vite.config.js"),
}

class ReactPage(private val project: Project): ComponentProvider {
    private val logger = logger<ReactPage>()
    private val routes: MutableMap<RouterFile, JSFile> = mutableMapOf()
    private val pages: MutableList<Component> = mutableListOf()
    private val components: MutableList<Component> = mutableListOf()

    // config files
    private val configs: MutableList<JSFile> = mutableListOf()

    init {
        val searchScope: GlobalSearchScope = ProjectScope.getContentScope(project)
        val psiManager = PsiManager.getInstance(project)

        val virtualFiles =
            FileTypeIndex.getFiles(JavaScriptFileType.INSTANCE, searchScope) +
                    FileTypeIndex.getFiles(TypeScriptJSXFileType.INSTANCE, searchScope)
//                    FileTypeIndex.getFiles(JSXHarmonyFileType.INSTANCE, searchScope)

        val root = project.guessProjectDir()!!

        virtualFiles.forEach { file ->
            val path = file.canonicalFile?.path ?: return@forEach

            val jsFile = (psiManager.findFile(file) ?: return@forEach) as? JSFile ?: return@forEach
            if (jsFile.isTestFile) return@forEach

            when {
                path.contains("pages") -> buildComponent(jsFile)?.let {
                    pages += it
                }

                path.contains("components") -> buildComponent(jsFile)?.let {
                    components += it
                }

                else -> {
                    if (root.findChild(file.name) != null) {
                        RouterFile.entries.filter { it.filename == file.name }.map {
                            routes += it to jsFile
                        }

                        configs.add(jsFile)
                    }
                }
            }
        }
    }

    override fun getPages(): List<Component> = pages

    override fun getComponents(): List<Component> = components

    private fun buildComponent(jsFile: JSFile): List<Component>? {
        return when (jsFile.language) {
            is TypeScriptJSXLanguageDialect,
            is ECMA6LanguageDialect,
                -> {
                val Components = ReactPsiUtil.tsxComponentToComponent(jsFile)
                if (Components.isEmpty()) {
                    logger.warn("no component found in ${jsFile.name}")
                }
                Components
            }

            else -> {
                logger.warn("unknown language: ${jsFile.language}")
                null
            }
        }
    }

    override fun getRoutes(): Map<String, String> {
        return this.routes.map {
            when (it.key) {
                RouterFile.UMI -> emptyMap()
                RouterFile.NEXT -> {
                    pages.associate { page ->
                        val route = page.name.replace(Regex("([A-Z])"), "-$1").lowercase()
                        route to route
                    }
                }

                RouterFile.VITE -> emptyMap()
            }
        }.reduce { acc, map -> acc + map }
    }

    /**
     * Retrieves a list of design system components from the ds.json file located in the prompts/context directory.
     * The method first attempts to locate the ds.json file by traversing the project directory structure.
     * If the file is found, it reads its content and decodes it as a JSON string into a list of [Component] objects.
     * In case of any exception during the reading or decoding process, an empty list is returned.
     *
     * @return a list of design system components parsed from the ds.json file, or an empty list if the file is not found
     *         or an error occurs during parsing.
     */
    fun getDesignSystemComponents(): List<Component> {
        val rootConfig = project.guessProjectDir()
            ?.findChild("prompts")
            ?.findChild("context")
            ?.findChild("ds.json") ?: return emptyList()

        val json = rootConfig.inputStream.reader().readText()
        return try {
            val result: List<Component> = Json.decodeFromString(json)
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun filterComponents(components: List<String>): List<Component> {
        val comps = this.pages + this.components
        return components.mapNotNull { component ->
            comps.find { it.name == component }
        }
    }
}

