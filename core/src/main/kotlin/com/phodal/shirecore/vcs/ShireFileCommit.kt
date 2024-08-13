package com.phodal.shirecore.vcs

data class ShireFileCommit(
    val filename: String,
    val path: String,
    val status: String,
    override val count: Int,
    override val commits: List<ShireGitCommit>
) : CommitModel(count, commits)