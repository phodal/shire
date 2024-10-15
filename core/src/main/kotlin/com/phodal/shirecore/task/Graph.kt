package com.phodal.shirecore.task

data class Node(val id: Int, val name: String)

data class Edge(val from: Node, val to: Node)

class Graph {
    private val nodes = mutableListOf<Node>()
    private val edges = mutableListOf<Edge>()
    private val adjList = mutableMapOf<Node, MutableList<Node>>()

    fun addNode(node: Node) {
        nodes.add(node)
        adjList[node] = mutableListOf()
    }

    fun addEdge(edge: Edge) {
        edges.add(edge)
        adjList[edge.from]?.add(edge.to)
    }

    fun getNodes(): List<Node> = nodes

    fun getEdges(): List<Edge> = edges

    fun getAdjList(): Map<Node, List<Node>> = adjList



    fun topologicalSort(graph: Graph): List<Node> {
        val visited = mutableSetOf<Node>()
        val stack = mutableListOf<Node>()
        val adjList = graph.getAdjList()

        fun dfs(node: Node) {
            if (node !in visited) {
                visited.add(node)
                adjList[node]?.forEach { dfs(it) }
                stack.add(node)
            }
        }

        graph.getNodes().forEach { if (it !in visited) dfs(it) }

        return stack.reversed()
    }

    fun hasCycle(graph: Graph): Boolean {
        val visited = mutableSetOf<Node>()
        val recStack = mutableSetOf<Node>()
        val adjList = graph.getAdjList()

        fun dfs(node: Node): Boolean {
            if (node !in visited) {
                visited.add(node)
                recStack.add(node)
                adjList[node]?.forEach {
                    if (it !in visited && dfs(it)) {
                        return true
                    } else if (it in recStack) {
                        return true
                    }
                }
            }
            recStack.remove(node)
            return false
        }

        return graph.getNodes().any { dfs(it) }
    }
}
