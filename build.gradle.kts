import groovy.util.Node
import groovy.xml.XmlParser
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.*
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformDependenciesExtension
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformTestingExtension
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask
import org.jetbrains.intellij.platform.gradle.utils.extensionProvider
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(properties("pluginVersion"))
    groups.empty()
    path.set(rootProject.file("CHANGELOG.md").toString())
    repositoryUrl.set(properties("pluginRepositoryUrl"))
}

/// maybe refs: https://github.com/HaxeFoundation/intellij-haxe/blob/develop/build.gradle.kts
/// and https://github.com/JetBrains/educational-plugin
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.gradleIntelliJPlugin) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
    alias(libs.plugins.serialization)

    id("org.jetbrains.grammarkit") version "2022.3.2.2"

    id("net.saliman.properties") version "1.5.2"
}

val ideaPlatformVersion = prop("ideaPlatformVersion").toInt()
val pluginProjects: List<Project> get() = rootProject.allprojects.toList()
val basePluginArchiveName = "intellij-shire"
val ideaPlugins = listOf(
    "org.jetbrains.plugins.terminal",
    "com.intellij.java",
    "org.jetbrains.plugins.gradle",
    "org.jetbrains.idea.maven",
    "JavaScript",
    "org.jetbrains.kotlin",
    "com.jetbrains.restClient"
) + if (ideaPlatformVersion == 243) {
    listOf(prop("jsonPlugin"))
} else {
    emptyList()
}

repositories {
    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

configure(
    subprojects
            - project(":languages")
            - project(":toolsets")
) {
    apply {
        plugin("org.jetbrains.intellij.platform.module")
    }

    intellijPlatform {
        instrumentCode = false
        buildSearchableOptions = false
    }

    tasks {
        prepareSandbox { enabled = false }
    }

    val testOutput = configurations.create("testOutput")

    dependencies {
        testOutput(sourceSets.test.get().output.classesDirs)

        intellijPlatform {
            testFramework(TestFrameworkType.Bundled)
        }
    }
}

configure(
    allprojects
            - project(":languages")
            - project(":toolsets")
) {
    apply {
        plugin("idea")
        plugin("kotlin")
        plugin("org.jetbrains.kotlinx.kover")
        plugin("org.jetbrains.intellij.platform.module")
    }

    repositories {
        mavenCentral()

        intellijPlatform {
            defaultRepositories()
        }
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/gen"))
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}

project(":core") {
    apply {
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    intellijPlatform {
        instrumentCode = false
    }

    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)

            pluginModule(implementation(project(":languages:shire-json")))

            testFramework(TestFrameworkType.Platform)
        }

        implementation(project(":languages:shire-json"))

        implementation("com.charleskorn.kaml:kaml:0.61.0")
        implementation("org.reflections:reflections:0.10.2")

        // chocolate factory
        // follow: https://onnxruntime.ai/docs/get-started/with-java.html
//        implementation("com.microsoft.onnxruntime:onnxruntime:1.19.2")
//        implementation("ai.djl.huggingface:tokenizers:0.29.0")

        implementation("cc.unitmesh:document:1.0.0")
        implementation("cc.unitmesh:cocoa-core:1.0.0")

        // custom agent deps
        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("com.squareup.okhttp3:okhttp:4.4.1")
        implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
        // open ai deps
        implementation("io.reactivex.rxjava3:rxjava:3.1.9")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    }
}

project(":languages:shire-java") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-javascript") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
            intellijPlugins(prop("nodejsPlugin"))
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-kotlin") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-markdown") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-python") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
            intellijPlugins(prop("platformPlugins"))
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-go") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
            intellijPlugins(prop("goPlugin"))
        }

        implementation(project(":core"))
    }
}

project(":languages:shire-json") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            if (ideaPlatformVersion == 243) {
                plugins(prop("jsonPlugin"))
            }

            intellijPlugins(ideaPlugins + if (ideaPlatformVersion == 243) "com.intellij.modules.json" else "")
        }
    }

    sourceSets {
        main {
            resources.srcDirs("src/$ideaPlatformVersion/main/resources")
        }
        test {
            resources.srcDirs("src/$ideaPlatformVersion/test/resources")
        }
    }
    kotlin {
        sourceSets {
            main {
                kotlin.srcDirs("src/main/kotlin", "src/$ideaPlatformVersion/main/kotlin")
            }
            test {
                kotlin.srcDirs("src/$ideaPlatformVersion/test/kotlin")
            }
        }
    }
}

project(":toolsets:git") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + "Git4Idea")
        }

        implementation(project(":core"))

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    }
}

project(":toolsets:httpclient") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
        }

        implementation(project(":core"))
        implementation(project(":languages:shire-json"))

        // custom agent deps
        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("com.squareup.okhttp3:okhttp:4.4.1")
        implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
        // open ai deps
        implementation("io.reactivex.rxjava3:rxjava:3.1.9")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    }
}

project(":toolsets:terminal") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins)
        }

        implementation(project(":core"))
    }
}

project(":toolsets:sonarqube") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + prop("sonarPlugin"))
        }

        implementation(project(":core"))
    }
}

project(":toolsets:plantuml") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + prop("plantUmlPlugin"))
        }

        implementation(project(":core"))
    }
}

project(":toolsets:mock") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + prop("wireMockPlugin"))
        }

        implementation(project(":core"))
    }
}

project(":toolsets:database") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + "com.intellij.database")
        }

        implementation(project(":core"))
    }
}

project(":toolsets:openwrite") {
    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + "com.intellij.openRewrite")
        }

        implementation(project(":core"))
    }
}

project(":shirelang") {
    apply {
        plugin("org.jetbrains.grammarkit")
    }

    dependencies {
        intellijPlatform {
            intellijIde(prop("ideaVersion"))
            intellijPlugins(ideaPlugins + "org.intellij.plugins.markdown" + "com.jetbrains.sh" + "Git4Idea")
        }

        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("org.apache.velocity:velocity-engine-core:2.4")

        // https://mvnrepository.com/artifact/com.huaban/jieba-analysis
        implementation("com.huaban:jieba-analysis:1.0.2")

        implementation("cc.unitmesh:cocoa-core:1.0.0")


        // for ShireQL Schema
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

        implementation(kotlin("reflect"))
        implementation(project(":core"))
        implementation(project(":languages:shire-json"))
    }

    tasks {
        generateLexer {
            sourceFile.set(file("src/main/grammar/ShireLexer.flex"))
            targetOutputDir.set(file("src/gen/com/phodal/shirelang/lexer"))
            purgeOldFiles.set(true)
        }

        generateParser {
            sourceFile.set(file("src/main/grammar/ShireParser.bnf"))
            targetRootOutputDir.set(file("src/gen"))
            pathToParser.set("com/phodal/shirelang/parser/ShireParser.java")
            pathToPsiRoot.set("com/phodal/shirelang/psi")
            purgeOldFiles.set(true)
        }

        withType<KotlinCompile> {
            dependsOn(generateLexer, generateParser)
        }
    }

    sourceSets {
        main {
            java.srcDirs("src/gen")
        }
    }
}

/**
 * Creates `run$[baseTaskName]` Gradle task to run IDE of given [type]
 * via `runIde` task with plugins according to [ideToPlugins] map
 */
fun IntelliJPlatformTestingExtension.customRunIdeTask(
    type: IntelliJPlatformType,
    versionWithCode: String? = null,
    baseTaskName: String = type.name,
) {
    runIde.register("run$baseTaskName") {
        useInstaller = false

        if (versionWithCode != null) {
            val version = versionWithCode.toTypeWithVersion().version

            this.type = type
            this.version = version
        } else {
            val pathProperty = baseTaskName.replaceFirstChar { it.lowercaseChar() } + "Path"
            // Avoid throwing exception during property calculation.
            // Some IDE tooling (for example, Package Search plugin) may try to calculate properties during `Sync` phase for all tasks.
            // In our case, some `run*` task may not have `pathProperty` in your `gradle.properties`,
            // and as a result, the `Sync` tool window will show you the error thrown by `prop` function.
            //
            // The following solution just moves throwing the corresponding error to task execution,
            // i.e., only when a task is actually invoked
            if (hasProp(pathProperty)) {
                localPath.convention(layout.dir(provider { file(prop(pathProperty)) }))
            } else {
                task {
                    doFirst {
                        throw GradleException("Property `$pathProperty` is not defined in gradle.properties")
                    }
                }
            }
        }

        // Specify custom sandbox directory to have a stable path to log file
        sandboxDirectory =
            intellijPlatform.sandboxContainer.dir("${baseTaskName.lowercase()}-sandbox-${prop("ideaPlatformVersion")}")

        plugins {
            plugins(ideaPlugins)
        }
    }
}

project(":") {
    apply {
        plugin("org.jetbrains.changelog")
        plugin("org.jetbrains.intellij.platform")
    }

    repositories {
        intellijPlatform {
            defaultRepositories()
            jetbrainsRuntime()
        }
    }

    intellijPlatform {
        projectName = basePluginArchiveName
        pluginConfiguration {
            id = "com.phodal.shire"
            name = "Shire - AI Coding Agent Language"
            version = prop("pluginVersion")

            ideaVersion {
                sinceBuild = prop("pluginSinceBuild")
                untilBuild = prop("pluginUntilBuild")
            }

            vendor {
                name = "Phodal Huang"
            }
        }

        pluginVerification {
            freeArgs = listOf("-mute", "TemplateWordInPluginId,ForbiddenPluginIdPrefix")
            failureLevel = listOf(VerifyPluginTask.FailureLevel.MISSING_DEPENDENCIES)
            ides {
                select {
                    sinceBuild = "242"
                    untilBuild = "243"
//                    sinceBuild = prop("pluginSinceBuild")
//                    untilBuild = prop("pluginUntilBuild")
                }
            }
        }

        instrumentCode = false
        buildSearchableOptions = false



        signing {
            certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
            privateKey = providers.environmentVariable("PRIVATE_KEY")
            password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
        }

        publishing {
            token = providers.environmentVariable("PUBLISH_TOKEN")
            channels.set(properties("pluginVersion").map {
                listOf(it.split('-').getOrElse(1) { "default" }.split('.').first())
            })
        }
    }

    dependencies {
        intellijPlatform {
            pluginVerifier()
            intellijIde(prop("ideaVersion"))
            if (hasProp("jbrVersion")) {
                jetbrainsRuntime(prop("jbrVersion"))
            } else {
                jetbrainsRuntime()
            }

            plugin(prop("jsonPlugin"))

            pluginModule(implementation(project(":core")))
            pluginModule(implementation(project(":shirelang")))
            pluginModule(implementation(project(":languages:shire-java")))
            pluginModule(implementation(project(":languages:shire-javascript")))
            pluginModule(implementation(project(":languages:shire-python")))
            pluginModule(implementation(project(":languages:shire-kotlin")))
            pluginModule(implementation(project(":languages:shire-go")))
            pluginModule(implementation(project(":languages:shire-markdown")))
//            pluginModule(implementation(project(":languages:shire-json")))
            pluginModule(implementation(project(":toolsets:git")))
            pluginModule(implementation(project(":toolsets:httpclient")))
            pluginModule(implementation(project(":toolsets:terminal")))
            pluginModule(implementation(project(":toolsets:sonarqube")))
            pluginModule(implementation(project(":toolsets:database")))
            pluginModule(implementation(project(":toolsets:mock")))

            testFramework(TestFrameworkType.Bundled)
        }

        // custom agent deps
        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("com.squareup.okhttp3:okhttp:4.4.1")
        implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
        // open ai deps
        implementation("io.reactivex.rxjava3:rxjava:3.1.9")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    }

//    kotlin {
//        sourceSets {
//            main {
//                resources.srcDirs("src/$ideaPlatformVersion/main/resources")
//            }
//        }
//    }

    tasks {
        val projectName = project.extensionProvider.flatMap { it.projectName }

        composedJar {
            archiveBaseName.convention(projectName)
        }

        withType<RunIdeTask> {
            // Disable auto plugin reloading. See `com.intellij.ide.plugins.DynamicPluginVfsListener`
            // To enable dynamic reloading, change value to `true` and disable `EduDynamicPluginListener`
            autoReload = false
            jvmArgs("-Xmx2g")
            jvmArgs("-Dide.experimental.ui=true")

            // Uncomment to show localized messages
            // jvmArgs("-Didea.l10n=true")

            // Uncomment to enable memory dump creation if plugin cannot be unloaded by the platform
            // jvmArgs("-Dide.plugins.snapshot.on.unload.fail=true")

            // Uncomment to enable FUS testing mode
            // jvmArgs("-Dfus.internal.test.mode=true")
        }

        patchPluginXml {
            pluginDescription.set(provider { file("src/description.html").readText() })

            changelog {
                version.set(properties("pluginVersion"))
                groups.empty()
                path.set(rootProject.file("CHANGELOG.md").toString())
                repositoryUrl.set(properties("pluginRepositoryUrl"))
            }

            val changelog = project.changelog
            // Get the latest available change notes from the changelog file
            changeNotes.set(properties("pluginVersion").map { pluginVersion ->
                with(changelog) {
                    renderItem(
                        (getOrNull(pluginVersion) ?: getUnreleased())
                            .withHeader(false)
                            .withEmptySections(false),

                        Changelog.OutputType.HTML,
                    )
                }
            })
        }

        val newName = "intellij-shire-" + prop("ideaVersion") + "-" + prop("pluginVersion")

        publishPlugin {
            dependsOn("patchChangelog")
            archiveFile = file("build/distributions/$newName.zip")
        }

        buildPlugin {
            archiveBaseName.convention(newName)
        }

        intellijPlatformTesting {
            // Generates event scheme for JetBrains Academy plugin FUS events to `build/eventScheme.json`
            runIde.register("buildEventsScheme") {
                task {
                    args(
                        "buildEventsScheme",
                        "--outputFile=${buildDir()}/eventScheme.json",
                        "--pluginId=com.jetbrains.edu"
                    )
                    // Force headless mode to be able to run command on CI
                    systemProperty("java.awt.headless", "true")
                    // BACKCOMPAT: 2024.1. Update value to 242 and this comment
                    // `IDEA_BUILD_NUMBER` variable is used by `buildEventsScheme` task to write `buildNumber` to output json.
                    // It will be used by TeamCity automation to set minimal IDE version for new events
                    environment("IDEA_BUILD_NUMBER", "241")
                }
            }

            runIde.register("runInSplitMode") {
                splitMode = true

                // Specify custom sandbox directory to have a stable path to log file
                sandboxDirectory = intellijPlatform.sandboxContainer.dir("split-mode-sandbox-$ideaPlatformVersion")

                plugins {
                    plugins(ideaPlugins)
                }
            }

            customRunIdeTask(IntellijIdeaUltimate, prop("ideaVersion"), baseTaskName = "Idea")
        }
    }
}

fun File.isPluginJar(): Boolean {
    if (!isFile) return false
    if (extension != "jar") return false
    return zipTree(this).files.any { it.isManifestFile() }
}

fun File.isManifestFile(): Boolean {
    if (extension != "xml") return false
    val rootNode = try {
        val parser = XmlParser()
        parser.parse(this)
    } catch (e: Exception) {
        logger.error("Failed to parse $path", e)
        return false
    }
    return rootNode.name() == "idea-plugin"
}

data class TypeWithVersion(val type: IntelliJPlatformType, val version: String)

fun String.toTypeWithVersion(): TypeWithVersion {
    val (code, version) = split("-", limit = 2)
    return TypeWithVersion(IntelliJPlatformType.fromCode(code), version)
}

fun IntelliJPlatformDependenciesExtension.intellijIde(versionWithCode: String) {
    val (type, version) = versionWithCode.toTypeWithVersion()
    create(type, version, useInstaller = false)
}

fun IntelliJPlatformDependenciesExtension.intellijPlugins(vararg notations: String) {
    for (notation in notations) {
        if (notation.contains(":")) {
            plugin(notation)
        } else {
            bundledPlugin(notation)
        }
    }
}

fun IntelliJPlatformDependenciesExtension.intellijPlugins(notations: List<String>) {
    intellijPlugins(*notations.toTypedArray())
}

fun hasProp(name: String): Boolean = extra.has(name)

fun prop(name: String): String =
    extra.properties[name] as? String ?: error("Property `$name` is not defined in gradle.properties")

fun withProp(name: String, action: (String) -> Unit) {
    if (hasProp(name)) {
        action(prop(name))
    }
}

fun withProp(filePath: String, name: String, action: (String) -> Unit) {
    if (!file(filePath).exists()) {
        println("$filePath doesn't exist")
        return
    }
    val properties = loadProperties(filePath)
    val value = properties.getProperty(name) ?: return
    action(value)
}

fun buildDir(): String {
    return project.layout.buildDirectory.get().asFile.absolutePath
}

fun <T : ModuleDependency> T.excludeKotlinDeps() {
    exclude(module = "kotlin-runtime")
    exclude(module = "kotlin-reflect")
    exclude(module = "kotlin-stdlib")
    exclude(module = "kotlin-stdlib-common")
    exclude(module = "kotlin-stdlib-jdk8")
}

fun loadProperties(path: String): Properties {
    val properties = Properties()
    file(path).bufferedReader().use { properties.load(it) }
    return properties
}

fun parseManifest(file: File): Node {
    val node = XmlParser().parse(file)
    check(node.name() == "idea-plugin") {
        "Manifest file `$file` doesn't contain top-level `idea-plugin` attribute"
    }
    return node
}

fun manifestFile(project: Project): File? {
    var filePath: String? = null

    val mainOutput = project.sourceSets.main.get().output
    val resourcesDir = mainOutput.resourcesDir ?: error("Failed to find resources dir for ${project.name}")

    if (filePath != null) {
        return resourcesDir.resolve(filePath).takeIf { it.exists() }
            ?: error("Failed to find manifest file for ${project.name} module")
    }
    val rootManifestFile =
        manifestFile(project(":intellij-plugin")) ?: error("Failed to find manifest file for :intellij-plugin module")
    val rootManifest = parseManifest(rootManifestFile)
    val children = ((rootManifest["content"] as? List<*>)?.single() as? Node)?.children()
        ?: error("Failed to find module declarations in root manifest")
    return children.filterIsInstance<Node>()
        .flatMap { node ->
            if (node.name() != "module") return@flatMap emptyList()
            val name = node.attribute("name") as? String ?: return@flatMap emptyList()
            listOfNotNull(resourcesDir.resolve("$name.xml").takeIf { it.exists() })
        }.firstOrNull() ?: error("Failed to find manifest file for ${project.name} module")
}

fun findModulePackage(project: Project): String? {
    val moduleManifest = manifestFile(project) ?: return null
    val node = parseManifest(moduleManifest)
    return node.attribute("package") as? String ?: error("Failed to find package for ${project.name}")
}

fun verifyClasses(project: Project) {
    val pkg = findModulePackage(project) ?: return
    val expectedDir = pkg.replace('.', '/')

    var hasErrors = false
    for (classesDir in project.sourceSets.main.get().output.classesDirs) {
        val basePath = classesDir.toPath()
        for (file in classesDir.walk()) {
            if (file.isFile && file.extension == "class") {
                val relativePath = basePath.relativize(file.toPath())
                if (!relativePath.startsWith(expectedDir)) {
                    logger.error(
                        "Wrong package of `${
                            relativePath.joinToString(".").removeSuffix(".class")
                        }` class. Expected `$pkg`"
                    )
                    hasErrors = true
                }
            }
        }
    }

    if (hasErrors) {
        throw GradleException("Classes with wrong package were found. See https://docs.google.com/document/d/1pOy-qNlGOJe6wftHVYHkH8sZOoAfav1fdGDPJgkQWJo")
    }
}

fun DependencyHandler.implementationWithoutKotlin(dependencyNotation: Provider<*>) {
    implementation(dependencyNotation) {
        excludeKotlinDeps()
    }
}

fun DependencyHandler.testImplementationWithoutKotlin(dependencyNotation: Provider<*>) {
    testImplementation(dependencyNotation) {
        excludeKotlinDeps()
    }
}
