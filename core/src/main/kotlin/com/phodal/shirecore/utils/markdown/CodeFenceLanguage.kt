package com.phodal.shirecore.utils.markdown

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.PlainTextLanguage

object CodeFenceLanguage {
    fun lookupFileExt(languageId: String): String {
        return when (languageId.lowercase()) {
            "c#" -> "cs"
            "c++" -> "cpp"
            "c" -> "c"
            "java" -> "java"
            "javascript" -> "js"
            "kotlin" -> "kt"
            "python" -> "py"
            "ruby" -> "rb"
            "swift" -> "swift"
            "typescript" -> "ts"
            "markdown" -> "md"
            "sql" -> "sql"
            "plantuml" -> "puml"
            "shell" -> "sh"
            "objective-c" -> "m"
            "objective-c++" -> "mm"
            "go" -> "go"
            "html" -> "html"
            "css" -> "css"
            "dart" -> "dart"
            "scala" -> "scala"
            "rust" -> "rs"
            "http request" -> "http"
            else -> languageId
        }
    }

    /**
     * Searches for a language by its name and returns the corresponding [Language] object. If the language is not found,
     * [PlainTextLanguage.INSTANCE] is returned.
     *
     * @param languageName The name of the language to find.
     * @return The [Language] object corresponding to the given name, or [PlainTextLanguage.INSTANCE] if the language is not found.
     */
    fun findLanguage(languageName: String): Language {
        val fixedLanguage = when (languageName) {
            "csharp" -> "c#"
            "cpp" -> "c++"
            "shell" -> "Shell Script"
            "sh" -> "Shell Script"
            "http" -> "HTTP Request"
            else -> languageName
        }

        val languages = Language.getRegisteredLanguages()
        val registeredLanguages = languages
            .filter { it.displayName.isNotEmpty() }

        return registeredLanguages.find { it.displayName.equals(fixedLanguage, ignoreCase = true) }
            ?: PlainTextLanguage.INSTANCE
    }
}