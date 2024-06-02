rootProject.name = "intellij-shire"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "plugin",
    // idea core
    "core",
    // cross-platform core?
    "core-api",
    "shirelang",

    "languages:java",
    "languages:shell",

    "toolsets:git",
    "toolsets:httpclient",
)
