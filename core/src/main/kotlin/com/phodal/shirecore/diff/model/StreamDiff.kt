package com.phodal.shirecore.diff.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class DiffLine {
    data class New(val line: String) : DiffLine()
    data class Old(val line: String) : DiffLine()
    data class Same(val line: String) : DiffLine()
}

data class MatchResult(val matchIndex: Int, val isPerfectMatch: Boolean, val newLine: String)

fun matchLine(newLine: String, oldLines: List<String>, seenIndentationMistake: Boolean): MatchResult {
    for ((index, oldLine) in oldLines.withIndex()) {
        if (oldLine == newLine) {
            return MatchResult(index, true, newLine)
        } else if (!seenIndentationMistake && oldLine.trim() == newLine.trim()) {
            return MatchResult(index, false, oldLine)
        }
    }
    return MatchResult(-1, false, newLine)
}

/**
 * https://blog.jcoglan.com/2017/02/12/the-myers-diff-algorithm-part-1/
 * Invariants:
 * - new + same = newLines.length
 * - old + same = oldLinesCopy.length
 * ^ (above two guarantee that all lines get represented)
 * - Lines are always output in order, at least among old and new separately
 * - Old lines in a hunk are always output before the new lines
 */
fun streamDiff(oldLines: List<String>, newLines: Flow<String>): Flow<DiffLine> = flow {
    val oldLinesCopy = oldLines.toMutableList()
    var seenIndentationMistake = false

    newLines.collect { newLine ->
        val (matchIndex, isPerfectMatch, matchedNewLine) = matchLine(newLine, oldLinesCopy, seenIndentationMistake)

        if (!seenIndentationMistake && newLine != matchedNewLine) {
            seenIndentationMistake = true
        }

        when {
            matchIndex == -1 -> {
                emit(DiffLine.New(newLine))
            }

            isPerfectMatch -> {
                emit(DiffLine.Same(oldLinesCopy.removeAt(0)))
            }

            else -> {
                for (i in 0 until matchIndex) {
                    emit(DiffLine.Old(oldLinesCopy.removeAt(0)))
                }
                emit(DiffLine.Old(oldLinesCopy.removeAt(0)))
                if (oldLinesCopy.firstOrNull() != newLine) {
                    emit(DiffLine.New(newLine))
                }
            }
        }
    }

    // Process remaining old lines
    for (oldLine in oldLinesCopy) {
        emit(DiffLine.Old(oldLine))
    }
}