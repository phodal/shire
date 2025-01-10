package com.phodal.shirelang.javascript.variable

import com.intellij.javascript.nodejs.PackageJsonDependency
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext
import com.phodal.shirelang.javascript.util.JsDependenciesSnapshot
import com.phodal.shirelang.javascript.util.LanguageApplicableUtil

class JavaScriptLanguageToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return LanguageApplicableUtil.isWebChatCreationContextSupported(context.sourceFile)
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val results = mutableListOf<ToolchainContextItem>()
        val snapshot = JsDependenciesSnapshot.create(project, context.sourceFile)

        val preferType = if (LanguageApplicableUtil.isPreferTypeScript(context)) "TypeScript" else "JavaScript"

        results.add(
            ToolchainContextItem(
                JavaScriptLanguageToolchainProvider::class,
                "You are working on a project that uses $preferType language"
            )
        )

        if (preferType == "TypeScript") {
            getTypeScriptLanguageContext(snapshot)?.let { results.add(it) }
        }

        getMostPopularPackagesContext(snapshot)?.let { results.add(it) }
        getJsWebFrameworksContext(snapshot)?.let { results.add(it) }
        getTestFrameworksContext(snapshot)?.let { results.add(it) }

        return results
    }

    private fun getTypeScriptLanguageContext(snapshot: JsDependenciesSnapshot): ToolchainContextItem? {
        val packageJson = snapshot.packages["typescript"] ?: return null
        val version = packageJson.parseVersion()
        return ToolchainContextItem(
            JavaScriptLanguageToolchainProvider::class,
            "The project uses TypeScript language" + (version?.let { ", version: $version" } ?: "")
        )
    }

    private fun getMostPopularPackagesContext(snapshot: JsDependenciesSnapshot): ToolchainContextItem? {
        val dependencies = snapshot.mostPopularFrameworks()
        return dependencies.takeIf { it.isNotEmpty() }?.let {
            ToolchainContextItem(
                JavaScriptLanguageToolchainProvider::class,
                "The project uses the following JavaScript packages: ${it.joinToString(", ")}"
            )
        }
    }

    private fun getJsWebFrameworksContext(snapshot: JsDependenciesSnapshot): ToolchainContextItem? {
        val frameworks = collectFrameworks(snapshot, JsWebFrameworks.entries)
        return frameworks.takeIf { it.isNotEmpty() }?.let {
            ToolchainContextItem(
                JavaScriptLanguageToolchainProvider::class,
                "The project uses the following JavaScript component frameworks: $it"
            )
        }
    }

    private fun getTestFrameworksContext(snapshot: JsDependenciesSnapshot): ToolchainContextItem? {
        val frameworks = collectFrameworks(snapshot, JsTestFrameworks.entries)
        return frameworks.takeIf { it.isNotEmpty() }?.let {
            ToolchainContextItem(
                JavaScriptLanguageToolchainProvider::class,
                "The project uses $it to test."
            )
        }
    }

    private fun collectFrameworks(
        snapshot: JsDependenciesSnapshot,
        frameworks: List<Framework>,
    ): Map<String, Boolean> {
        return snapshot.packages.filter { (_, entry) ->
            entry.dependencyType == PackageJsonDependency.dependencies ||
                    entry.dependencyType == PackageJsonDependency.devDependencies
        }.mapNotNull { (name, _) ->
            frameworks.find { name.startsWith(it.packageName) || name == it.packageName }?.packageName
        }.associateWith { true }
    }
}