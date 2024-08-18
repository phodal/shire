package com.phodal.shirecore.variable.vcs

data class ShireFileBranch(
    val name: String,
    override val count: Int,
    override val commits: List<ShireGitCommit>
) : CommitModel(count, commits)