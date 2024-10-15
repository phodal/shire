package com.phodal.shirecore.task

import org.junit.Assert.*
import org.junit.Test


class GraphTest {

    @Test
    fun should_addNode_toGraph_when_addNode() {
        // given
        val graph = Graph()
        val node = Node(1, "Node1")

        // when
        graph.addNode(node)

        // then
        assertEquals(listOf(node), graph.getNodes())
    }

    @Test
    fun should_addEdge_toGraph_when_addEdge() {
        // given
        val graph = Graph()
        val node1 = Node(1, "Node1")
        val node2 = Node(2, "Node2")
        val edge = Edge(node1, node2)

        // when
        graph.addNode(node1)
        graph.addNode(node2)
        graph.addEdge(edge)

        // then
        assertEquals(listOf(edge), graph.getEdges())
    }

    @Test
    fun should_returnTopologicalSort_when_topologicalSort() {
        // given
        val graph = Graph()
        val node1 = Node(1, "Node1")
        val node2 = Node(2, "Node2")
        val node3 = Node(3, "Node3")
        val edge1 = Edge(node1, node2)
        val edge2 = Edge(node2, node3)

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addNode(node3)
        graph.addEdge(edge1)
        graph.addEdge(edge2)

        // when
        val result = graph.topologicalSort(graph)

        // then
        assertEquals(listOf(node1, node2, node3), result)
    }

    @Test
    fun should_returnTrue_when_hasCycle() {
        // given
        val graph = Graph()
        val node1 = Node(1, "Node1")
        val node2 = Node(2, "Node2")
        val node3 = Node(3, "Node3")
        val edge1 = Edge(node1, node2)
        val edge2 = Edge(node2, node3)
        val edge3 = Edge(node3, node1)

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addNode(node3)
        graph.addEdge(edge1)
        graph.addEdge(edge2)
        graph.addEdge(edge3)

        // when
        val result = graph.hasCycle(graph)

        // then
        assertTrue(result)
    }

    @Test
    fun should_returnFalse_when_noCycle() {
        // given
        val graph = Graph()
        val node1 = Node(1, "Node1")
        val node2 = Node(2, "Node2")
        val node3 = Node(3, "Node3")
        val edge1 = Edge(node1, node2)
        val edge2 = Edge(node2, node3)

        graph.addNode(node1)
        graph.addNode(node2)
        graph.addNode(node3)
        graph.addEdge(edge1)
        graph.addEdge(edge2)

        // when
        val result = graph.hasCycle(graph)

        // then
        assertFalse(result)
    }
}
