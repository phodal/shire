rootProject.name = "intellij-shire"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "plugin",
    "core",
    "language",
)
