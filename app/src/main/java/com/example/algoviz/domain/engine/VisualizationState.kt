package com.example.algoviz.domain.engine

/**
 * Represents the fundamental structural state of any data structure visualized on the screen.
 * Distinct from [VisualizationStep] which wraps this state alongside an explanation.
 */
sealed class VisualizationState {
    
    /**
     * Represents a standard 1D Array or List layout.
     */
    data class ArrayState(
        val array: List<VisualNode>,
        val comparingIndices: List<Int> = emptyList(),
        val swapped: Boolean = false,
        val sortedIndices: Set<Int> = emptySet()
    ) : VisualizationState()

    /**
     * Represents a Graph connecting nodes via edges.
     */
    data class GraphState(
        val nodes: List<GraphNode>,
        val edges: List<GraphEdge>,
        val visitedNodes: Set<Int> = emptySet(),
        val activePathEdges: Set<Pair<Int, Int>> = emptySet(),
        val currentNode: Int? = null
    ) : VisualizationState()

    /**
     * Represents a Hierarchical Tree (like a BST).
     */
    data class TreeState(
        val root: TreeNode?,
        val highlightedNodes: Set<Int> = emptySet(),
        val comparingNode: Int? = null,
        val foundNode: Int? = null
    ) : VisualizationState()
}

// Support definitions for Arrays, Graphs and Trees
data class VisualNode(val id: String, val value: Int)
data class GraphNode(val id: Int, val value: String = id.toString())
data class GraphEdge(val from: Int, val to: Int, val weight: String? = null, val isDirected: Boolean = false)

class TreeNode(
    val id: Int,
    val value: Int,
    var left: TreeNode? = null,
    var right: TreeNode? = null
)
