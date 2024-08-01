package com.phodal.shirecore.guard.secret

import java.util.regex.Pattern

sealed class RegexBasedDetector {
    abstract val description: String
    abstract val denylist: List<Pattern>
}

class GitHubTokenCustomDetector : RegexBasedDetector() {
    override val description: String = "GitHub Token"

    override val denylist: List<Pattern> = listOf(
        // GitHub App/Personal Access/OAuth Access/Refresh Token
        Pattern.compile("(?:ghp|gho|ghu|ghs|ghr)_[A-Za-z0-9_]{36}"),
        // GitHub Fine-Grained Personal Access Token
        Pattern.compile("github_pat_[0-9a-zA-Z_]{82}"),
        Pattern.compile("gho_[0-9a-zA-Z]{36}")
    )
}

class JWTBase64Detector : RegexBasedDetector() {
    override val description: String = "Base64-encoded JSON Web Token"

    override val denylist: List<Pattern> = listOf(
        Pattern.compile("[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+")
    )
}

class OpenAIApiKeyDetector : RegexBasedDetector() {
    override val description: String = "OpenAI API Key"

    override val denylist: List<Pattern> = listOf(
        Pattern.compile("""(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|$)""")
    )
}