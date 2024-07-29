package com.phodal.shirecore.vcs

/**
 * author, authorEmail, committer, committerEmail, hash, date, message, fullMessage
 */
data class ShireVcsCommit(
    val authorName: String,
    val authorEmail: String,
    val committerName: String,
    val committerEmail: String,
    val hash: String,
    val date: String,
    val message: String,
    val fullMessage: String
)

data class ShireFileCommit(
    val filename: String,
    val path: String,
    val status: String,
    val count: Int,
    val commits: List<ShireVcsCommit>
)

data class ShireFileBranch(
    val name: String,
    val count: Int,
    val commits: List<ShireVcsCommit>
)

