package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.GraphEdge
import com.example.algoviz.domain.engine.GraphNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep

class BFSVisualizer : AlgorithmVisualizer {

    // Initial data for graph implementations should either be a specialized Graph Input class or
    // we can parse an Adjacency List Map. For BFS, we'll accept Map<Int, List<Int>>.
    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val adjacencyList = initialData as? Map<Int, List<Int>> 
            ?: throw IllegalArgumentException("BFS expects Map<Int, List<Int>> representing an adjacency list.")

        val steps = mutableListOf<VisualizationStep>()
        
        // Construct canonical nodes and edges for rendering
        val nodes = adjacencyList.keys.map { GraphNode(it) }
        val edges = mutableListOf<GraphEdge>()
        
        // Track edges we've already added so we don't draw undirected edges twice
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
        val queue = ArrayDeque<Int>()
        val visited = mutableSetOf<Int>()
        val activePathEdges = mutableSetOf<Pair<Int, Int>>()

        // Step 1: Initial state
        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = emptySet(),
                    currentNode = null
                ),
                explanation = "Initializing Breadth-First Search. Starting at node $startNode.",
                activeLine = 2
            )
        )

        queue.addLast(startNode)
        visited.add(startNode)

        // Step 2: Push root
        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = activePathEdges.toSet(),
                    currentNode = startNode
                ),
                explanation = "Node $startNode is added to the queue and marked as visited.",
                activeLine = 4
            )
        )

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            
            // Pop current
            steps.add(
                VisualizationStep(
                    state = VisualizationState.GraphState(
                        nodes = nodes,
                        edges = edges,
                        visitedNodes = visited.toSet(),
                        activePathEdges = activePathEdges.toSet(),
                        currentNode = curr
                    ),
                    explanation = "Dequeue node $curr. Now exploring its neighbors.",
                    activeLine = 7
                )
            )

            val neighbors = adjacencyList[curr] ?: emptyList()
            for (neighbor in neighbors) {
                // Highlight edge evaluation
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
                        activeLine = 8
                    )
                )

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.addLast(neighbor)
                    
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.GraphState(
                                nodes = nodes,
                                edges = edges,
                                visitedNodes = visited.toSet(),
                                activePathEdges = activePathEdges.toSet(),
                                currentNode = neighbor // Highlight the newly found neighbor temporarily
                            ),
                            explanation = "Node $neighbor has not been visited. Marking it as visited and adding to queue.",
                            activeLine = 10
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
                            activeLine = 9
                        )
                    )
                }
                
                // Clear the active edge focus after looking at this neighbor
                activePathEdges.remove(Pair(curr, neighbor))
            }
        }

        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = emptySet(),
                    currentNode = null
                ),
                explanation = "Queue is empty. BFS traversal is complete."
            )
        )

        return steps
    }
}
