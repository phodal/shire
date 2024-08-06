package com.phodal.shirecore.vcs

sealed class GitEntity

// Base class for models containing commits
sealed class CommitModel(
    open val count: Int,
    open val commits: List<ShireGitCommit>
) : GitEntity()

data class ShireGitCommit(
    val hash: String,
    val authorName: String,
    val authorEmail: String,
    val authorDate: Long,
    val committerName: String,
    val committerEmail: String,
    val committerDate: Long,
    val message: String,
    val fullMessage: String
) : GitEntity()

data class ShireFileCommit(
    val filename: String,
    val path: String,
    val status: String,
    override val count: Int,
    override val commits: List<ShireGitCommit>
) : CommitModel(count, commits)

data class ShireFileBranch(
    val name: String,
    override val count: Int,
    override val commits: List<ShireGitCommit>
) : CommitModel(count, commits)
