package com.phodal.shirecore.diff.model

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StreamDiffTest {
    @Test
    fun `should emit New DiffLine when no match found in old lines`() = runBlocking<Unit> {
        // given
        val oldLines = listOf("old line 1", "old line 2")
        val newLines = flowOf("new line 1", "new line 2")

        // when
        val result = streamDiff(oldLines, newLines).toList()

        // then
        assertThat(result).containsExactly(
            DiffLine.New("new line 1"),
            DiffLine.New("new line 2"),
            DiffLine.Old("old line 1"),
            DiffLine.Old("old line 2")
        )
    }

    @Test
    fun `should emit Same DiffLine when perfect match is found`() = runBlocking<Unit> {
        // given
        val oldLines = listOf("line 1", "line 2", "line 3")
        val newLines = flowOf("line 1", "line 2", "line 3")

        // when
        val result = streamDiff(oldLines, newLines).toList()

        // then
        assertThat(result).containsExactly(
            DiffLine.Same("line 1"),
            DiffLine.Same("line 2"),
            DiffLine.Same("line 3")
        )
    }

    @Test
    fun `should emit Old and New DiffLine when match is found but not perfect`() = runBlocking<Unit> {
        // given
        val oldLines = listOf(" line 1", "line 2", "line 3")
        val newLines = flowOf("line 1", " line 2", "line 3")

        // when
        val result = streamDiff(oldLines, newLines).toList()

        // [Old(line= line 1), New(line=line 1), New(line= line 2), Same(line=line 2), Old(line=line 3)]
        assertThat(result).containsExactly(
            DiffLine.Old(" line 1"),
            DiffLine.New("line 1"),
            DiffLine.New(" line 2"),
            DiffLine.Same("line 2"),
            DiffLine.Old("line 3")
        )
    }

    @Test
    fun `should emit Old DiffLine for remaining old lines when new lines are exhausted`() = runBlocking<Unit> {
        // given
        val oldLines = listOf("line 1", "line 2", "line 3", "line 4")
        val newLines = flowOf("line 1", "line 2", "line 3")

        // when
        val result = streamDiff(oldLines, newLines).toList()

        // then
        assertThat(result).containsExactly(
            DiffLine.Same("line 1"),
            DiffLine.Same("line 2"),
            DiffLine.Same("line 3"),
            DiffLine.Old("line 4")
        )
    }

    @Test
    fun `should handle seen indentation mistake correctly`() = runBlocking<Unit> {
        // given
        val oldLines = listOf(" line 1", "line 2", "line 3")
        val newLines = flowOf("line 1", "  line 2", "line 3")

        // when
        val result = streamDiff(oldLines, newLines).toList()

        // then
        //[Old(line= line 1), New(line=line 1), New(line=  line 2), Same(line=line 2), Old(line=line 3)]
        assertThat(result).containsExactly(
            DiffLine.Old(" line 1"),
            DiffLine.New("line 1"),
            DiffLine.New("  line 2"),
            DiffLine.Same("line 2"),
            DiffLine.Old("line 3")
        )
    }
}
