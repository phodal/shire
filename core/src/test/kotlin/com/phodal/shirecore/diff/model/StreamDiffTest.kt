package com.phodal.shirecore.diff.model

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class StreamDiffTest {
    @Test
    fun `add multiple lines`() = runBlocking {
        val oldLines = listOf("first item", "fourth val")
        val newLines = flowOf("first item", "second arg", "third param", "fourth val")

        val diffResults = streamDiff(oldLines, newLines).toList()

        assertEquals(
            listOf(
                DiffLine.Same("first item"),
                DiffLine.New("second arg"),
                DiffLine.New("third param"),
                DiffLine.Same("fourth val")
            ),
            diffResults
        )
    }

    @Test
    fun `remove multiple lines`() = runBlocking {
        val oldLines = listOf("first item", "second arg", "third param", "fourth val")
        val newLines = flowOf("first item", "fourth val")

        val diffResults = streamDiff(oldLines, newLines).toList()

        assertEquals(
            listOf(
                DiffLine.Same("first item"),
                DiffLine.Old("second arg"),
                DiffLine.Old("third param"),
                DiffLine.Same("fourth val")
            ),
            diffResults
        )
    }

    @Test
    fun `empty old lines`() = runBlocking {
        val oldLines = emptyList<String>()
        val newLines = flowOf("first item", "second arg")

        val diffResults = streamDiff(oldLines, newLines).toList()

        assertEquals(
            listOf(
                DiffLine.New("first item"),
                DiffLine.New("second arg")
            ),
            diffResults
        )
    }

    @Test
    fun `empty new lines`() = runBlocking {
        val oldLines = listOf("first item", "second arg")
        val newLines = flowOf<String>() // 空的新行流

        val diffResults = streamDiff(oldLines, newLines).toList()

        assertEquals(
            listOf(
                DiffLine.Old("first item"),
                DiffLine.Old("second arg")
            ),
            diffResults
        )
    }
}
