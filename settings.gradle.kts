rootProject.name = "intellij-shire"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "plugin",
    "core",
    "shirelang",
)

include(
    "languages:shire-java",
    "languages:shire-javascript",
    "languages:shire-python",
    "languages:shire-kotlin",
    "languages:shire-go",
    "languages:shire-markdown",
    "languages:shire-json",

    "toolsets:git",
    "toolsets:httpclient",
    "toolsets:terminal",
    "toolsets:sonarqube",
    "toolsets:plantuml",
    "toolsets:database",
    "toolsets:mock",
//    "toolsets:uitest",
)
