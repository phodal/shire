rootProject.name = "intellij-shire"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
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
    "languages:shire-proto",

    "toolsets:git",
    "toolsets:httpclient",
    "toolsets:terminal",
    "toolsets:sonarqube",
    "toolsets:plantuml",
    "toolsets:database",
    "toolsets:mock",
    "toolsets:openrewrite",
    "toolsets:mermaid",
//    "toolsets:uitest",
)
