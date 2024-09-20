import groovy.xml.XmlParser
import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.changelog.Changelog
import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.intellij.tasks.RunIdeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

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
}

fun prop(name: String): String =
    extra.properties[name] as? String
        ?: error("Property `$name` is not defined in gradle.properties")

val ideaPlatformVersion = prop("ideaPlatformVersion")
val pluginProjects: List<Project> get() = rootProject.allprojects.toList()
val basePluginArchiveName = "intellij-shire"
val ideaPlugins = listOf(
    "org.jetbrains.plugins.terminal",
    "com.intellij.java",
    "org.jetbrains.plugins.gradle",
    "org.jetbrains.idea.maven",
    "org.jetbrains.kotlin",
    "JavaScript",
    "com.jetbrains.restClient"
)


// Configure project's dependencies
repositories {
    mavenCentral()
}

allprojects {
    apply {
        plugin("idea")
        plugin("kotlin")
        plugin("org.jetbrains.intellij")
        plugin("org.jetbrains.kotlinx.kover")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    }

    intellij {
        version.set(prop("platformVersion"))
        type.set(prop("platformType"))
        instrumentCode.set(false)
        sandboxDir.set("${layout.projectDirectory}/build/idea-sandbox-$ideaPlatformVersion")
    }

    idea {
        module {
            generatedSourceDirs.add(file("src/gen"))
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = VERSION_17.toString()
                languageVersion = "1.8"
                // see https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#kotlin-standard-library
                apiVersion = "1.7"
                freeCompilerArgs = listOf("-Xjvm-default=all")
            }
        }

        withType<PatchPluginXmlTask> {
            sinceBuild.set(prop("pluginSinceBuild"))
            untilBuild.set(prop("pluginUntilBuild"))
        }

        // All these tasks don't make sense for non-root subprojects
        // Root project (i.e. `:plugin`) enables them itself if needed
        runIde { enabled = false }
        prepareSandbox { enabled = false }
        buildSearchableOptions { enabled = false }
    }
}

project(":core") {
    apply {
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    dependencies {
        implementation("com.charleskorn.kaml:kaml:0.61.0")
        implementation("org.reflections:reflections:0.10.2")

        // chocolate factory
        // follow: https://onnxruntime.ai/docs/get-started/with-java.html
//        implementation("com.microsoft.onnxruntime:onnxruntime:1.19.2")
//        implementation("ai.djl.huggingface:tokenizers:0.29.0")

        implementation("cc.unitmesh:document:1.0.0")
        implementation("cc.unitmesh:cocoa-core:1.0.0")
    }
}

project(":languages:shire-java") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":languages:shire-javascript") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("nodejsPlugin"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":languages:shire-kotlin") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":languages:shire-markdown") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":languages:shire-python") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("platformPlugins"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":languages:shire-go") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("goPlugin"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":toolsets:git") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + "Git4Idea")
    }

    dependencies {
        implementation(project(":core"))

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    }
}

project(":toolsets:httpclient") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))

        // custom agent deps
        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("com.squareup.okhttp3:okhttp:4.4.1")
        implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")
        // open ai deps
        implementation("io.reactivex.rxjava3:rxjava:3.1.9")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    }
}

project(":toolsets:terminal") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":toolsets:sonarqube") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("sonarPlugin"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":toolsets:plantuml") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("plantUmlPlugin"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":toolsets:mock") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + prop("wireMockPlugin"))
    }

    dependencies {
        implementation(project(":core"))
    }
}

project(":toolsets:database") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins + "com.intellij.database")
    }
    dependencies {
        implementation(project(":core"))
    }
}

//project(":toolsets:uitest") {
//    intellij {
//        version.set(prop("ideaVersion"))
//        plugins.set(ideaPlugins + prop("testAutomationPlugin"))
//    }
//    dependencies {
//        implementation(project(":core"))
//    }
//}

project(":shirelang") {
    apply {
        plugin("org.jetbrains.grammarkit")
    }

    intellij {
        version.set(prop("platformVersion"))
        plugins.set((listOf<String>() + "org.intellij.plugins.markdown" + "com.jetbrains.sh" + "Git4Idea"))
    }

    dependencies {
        implementation("com.nfeld.jsonpathkt:jsonpathkt:2.0.1")
        implementation("org.apache.velocity:velocity-engine-core:2.3")

        implementation("cc.unitmesh:cocoa-core:1.0.0")

        // for ShireQL Schema
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

        implementation(kotlin("reflect"))
        implementation(project(":"))
        implementation(project(":core"))
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

project(":plugin") {
    apply {
        plugin("org.jetbrains.changelog")
    }

    version = prop("pluginVersion")

    intellij {
        pluginName.set(basePluginArchiveName)
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":"))
        implementation(project(":core"))
        implementation(project(":shirelang"))

        implementation(project(":languages:shire-java"))
        implementation(project(":languages:shire-javascript"))
        implementation(project(":languages:shire-python"))
        implementation(project(":languages:shire-kotlin"))
        implementation(project(":languages:shire-go"))
        implementation(project(":languages:shire-markdown"))

        implementation(project(":toolsets:git"))
        implementation(project(":toolsets:httpclient"))
        implementation(project(":toolsets:terminal"))
        implementation(project(":toolsets:sonarqube"))
        implementation(project(":toolsets:database"))
        implementation(project(":toolsets:mock"))
//        implementation(project(":toolsets:uitest"))
    }

    // Collects all jars produced by compilation of project modules and merges them into singe one.
    // We need to put all plugin manifest files into single jar to make new plugin model work
    val mergePluginJarTask = task<Jar>("mergePluginJars") {
        duplicatesStrategy = DuplicatesStrategy.FAIL
        archiveBaseName.set(basePluginArchiveName)

        exclude("META-INF/MANIFEST.MF")
        exclude("**/classpath.index")

        val pluginLibDir by lazy {
            val sandboxTask = tasks.prepareSandbox.get()
            sandboxTask.destinationDir.resolve("${sandboxTask.pluginName.get()}/lib")
        }

        val pluginJars by lazy {
            pluginLibDir.listFiles().orEmpty().filter {
                it.isPluginJar()
            }
        }

        destinationDirectory.set(project.layout.dir(provider { pluginLibDir }))

        doFirst {
            for (file in pluginJars) {
                from(zipTree(file))
            }
        }

        doLast {
            delete(pluginJars)
        }
    }

    // Add plugin sources to the plugin ZIP.
    // gradle-intellij-plugin will use it as a plugin sources if the plugin is used as a dependency
    val createSourceJar = task<Jar>("createSourceJar") {
        for (prj in pluginProjects) {
            from(prj.kotlin.sourceSets.main.get().kotlin) {
                include("**/*.java")
                include("**/*.kt")
            }
        }

        destinationDirectory.set(layout.buildDirectory.dir("libs"))
        archiveBaseName.set(basePluginArchiveName)
        archiveClassifier.set("src")
    }

    tasks {
        buildPlugin {
            dependsOn(createSourceJar)
            from(createSourceJar) { into("lib/src") }
            // Set proper name for final plugin zip.
            // Otherwise, base name is the same as gradle module name
            archiveBaseName.set(basePluginArchiveName)
        }

        runIde { enabled = true }

        prepareSandbox {
            finalizedBy(mergePluginJarTask)
            enabled = true
        }

        buildSearchableOptions {
            // Force `mergePluginJarTask` be executed before `buildSearchableOptions`
            // Otherwise, `buildSearchableOptions` task can't load the plugin and searchable options are not built.
            // Should be dropped when jar merging is implemented in `gradle-intellij-plugin` itself
            dependsOn(mergePluginJarTask)
            enabled = false
        }

        withType<RunIdeTask> {
            // Default args for IDEA installation
            jvmArgs("-Xmx768m", "-XX:+UseG1GC", "-XX:SoftRefLRUPolicyMSPerMB=50")
            // Disable plugin auto reloading. See `com.intellij.ide.plugins.DynamicPluginVfsListener`
            jvmArgs("-Didea.auto.reload.plugins=false")
            // Don't show "Tip of the Day" at startup
            jvmArgs("-Dide.show.tips.on.startup.default.value=false")
            // uncomment if `unexpected exception ProcessCanceledException` prevents you from debugging a running IDE
            // jvmArgs("-Didea.ProcessCanceledException=disabled")
        }

        withType<PatchPluginXmlTask> {
            pluginDescription.set(provider { file("description.html").readText() })

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

        withType<PublishPluginTask> {
            dependsOn("patchChangelog")
            token.set(environment("PUBLISH_TOKEN"))
            channels.set(properties("pluginVersion").map {
                listOf(it.split('-').getOrElse(1) { "default" }.split('.').first())
            })
        }
    }
}

/// for customize and business logic
project(":") {
    intellij {
        version.set(prop("ideaVersion"))
        plugins.set(ideaPlugins)
    }

    dependencies {
        implementation(project(":core"))

        // custom agent deps
        implementation(libs.json.pathkt)
        implementation(libs.okhttp)
        implementation(libs.okhttp.sse)
        // open ai deps
        implementation("io.reactivex.rxjava3:rxjava:3.1.9")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    }

    task("resolveDependencies") {
        doLast {
            rootProject.allprojects
                .map { it.configurations }
                .flatMap { it.filter { c -> c.isCanBeResolved } }
                .forEach { it.resolve() }
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
