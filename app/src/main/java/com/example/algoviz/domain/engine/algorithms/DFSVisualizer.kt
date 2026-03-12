package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.GraphEdge
import com.example.algoviz.domain.engine.GraphNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep

class DFSVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val adjacencyList = initialData as? Map<Int, List<Int>>
            ?: throw IllegalArgumentException("DFS expects Map<Int, List<Int>> representing an adjacency list.")

        val steps = mutableListOf<VisualizationStep>()

        val nodes = adjacencyList.keys.map { GraphNode(it) }
        val edges = mutableListOf<GraphEdge>()

        val seenEdges = mutableSetOf<Pair<Int, Int>>()
        for ((node, neighbors) in adjacencyList) {
            for (neighbor in neighbors) {
                val pair1 = Pair(node, neighbor)
                val pair2 = Pair(neighbor, node)
                if (!seenEdges.contains(pair1) && !seenEdges.contains(pair2)) {
                    edges.add(GraphEdge(from = node, to = neighbor))
                    seenEdges.add(pair1)
                }
            }
        }

        val startNode = adjacencyList.keys.firstOrNull() ?: return emptyList()
        val visited = mutableSetOf<Int>()
        val activePathEdges = mutableSetOf<Pair<Int, Int>>()

        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = emptySet(),
                    currentNode = null
                ),
                explanation = "Initializing Depth-First Search. Starting at node $startNode.",
                activeLine = 1
            )
        )

        dfsRecursive(startNode, adjacencyList, visited, nodes, edges, activePathEdges, steps)

        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = emptySet(),
                    currentNode = null
                ),
                explanation = "DFS traversal is complete."
            )
        )

        return steps
    }

    private fun dfsRecursive(
        curr: Int,
        adjacencyList: Map<Int, List<Int>>,
        visited: MutableSet<Int>,
        nodes: List<GraphNode>,
        edges: List<GraphEdge>,
        activePathEdges: MutableSet<Pair<Int, Int>>,
        steps: MutableList<VisualizationStep>
    ) {
        visited.add(curr)

        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = activePathEdges.toSet(),
                    currentNode = curr
                ),
                explanation = "Node $curr is visited. Now exploring its descendants recursively.",
                activeLine = 2
            )
        )

        val neighbors = adjacencyList[curr] ?: emptyList()
        for (neighbor in neighbors) {
            activePathEdges.add(Pair(curr, neighbor))

            steps.add(
                VisualizationStep(
                    state = VisualizationState.GraphState(
                        nodes = nodes,
                        edges = edges,
                        visitedNodes = visited.toSet(),
                        activePathEdges = activePathEdges.toSet(),
                        currentNode = curr
                    ),
                    explanation = "Looking at edge from $curr to $neighbor.",
                    activeLine = 3
                )
            )

            if (!visited.contains(neighbor)) {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.GraphState(
                            nodes = nodes,
                            edges = edges,
                            visitedNodes = visited.toSet(),
                            activePathEdges = activePathEdges.toSet(),
                            currentNode = neighbor
                        ),
                        explanation = "Node $neighbor has not been visited. Traversing deeper.",
                        activeLine = 4
                    )
                )

                dfsRecursive(neighbor, adjacencyList, visited, nodes, edges, activePathEdges, steps)
                
                // Backtrack visualization
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.GraphState(
                            nodes = nodes,
                            edges = edges,
                            visitedNodes = visited.toSet(),
                            activePathEdges = activePathEdges.toSet(),
                            currentNode = curr
                        ),
                        explanation = "Backtracking from $neighbor to $curr.",
                        activeLine = 5
                    )
                )

            } else {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.GraphState(
                            nodes = nodes,
                            edges = edges,
                            visitedNodes = visited.toSet(),
                            activePathEdges = activePathEdges.toSet(),
                            currentNode = curr
                        ),
                        explanation = "Node $neighbor is already visited. Skipping.",
                        activeLine = 4
                    )
                )
            }

            activePathEdges.remove(Pair(curr, neighbor))
        }
    }
}
