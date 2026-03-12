package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.GraphEdge
import com.example.algoviz.domain.engine.GraphNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.PriorityQueue

data class DijkstraEdge(val to: Int, val weight: Int)

class DijkstraVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val adjacencyList = initialData as? Map<Int, List<DijkstraEdge>>
            ?: throw IllegalArgumentException("Dijkstra expects Map<Int, List<DijkstraEdge>>.")

        val steps = mutableListOf<VisualizationStep>()

        val nodes = adjacencyList.keys.map { GraphNode(it) }
        val edges = mutableListOf<GraphEdge>()

        // Construct directed edges for visualization with weights
        for ((node, neighbors) in adjacencyList) {
            for (neighbor in neighbors) {
                edges.add(GraphEdge(from = node, to = neighbor.to, weight = neighbor.weight.toString(), isDirected = true))
            }
        }

        val startNode = adjacencyList.keys.firstOrNull() ?: return emptyList()
        val distances = mutableMapOf<Int, Int>().apply {
            adjacencyList.keys.forEach { this[it] = Int.MAX_VALUE }
        }
        distances[startNode] = 0

        val visited = mutableSetOf<Int>()
        val activePathEdges = mutableSetOf<Pair<Int, Int>>()
        
        // Priority queue holds Pair<NodeId, CurrentDistance>
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
        priorityQueue.add(Pair(startNode, 0))

        steps.add(
            VisualizationStep(
                state = VisualizationState.GraphState(
                    nodes = nodes,
                    edges = edges,
                    visitedNodes = visited.toSet(),
                    activePathEdges = emptySet(),
                    currentNode = null
                ),
                explanation = "Initializing Dijkstra's Algorithm. Starting at node $startNode with distance 0. All other nodes have distance ∞.",
                activeLine = 2
            )
        )

        while (priorityQueue.isNotEmpty()) {
            val (currNode, currDist) = priorityQueue.poll() ?: continue

            if (visited.contains(currNode)) continue

            visited.add(currNode)

            steps.add(
                VisualizationStep(
                    state = VisualizationState.GraphState(
                        nodes = nodes.map { if (it.id == currNode) it.copy(value = "$currDist") else it },
                        edges = edges,
                        visitedNodes = visited.toSet(),
                        activePathEdges = activePathEdges.toSet(),
                        currentNode = currNode
                    ),
                    explanation = "Node $currNode is extracted from the Priority Queue with the shortest known distance: $currDist.",
                    activeLine = 8
                )
            )

            val neighbors = adjacencyList[currNode] ?: emptyList()

            for (neighbor in neighbors) {
                val nextNode = neighbor.to
                val weight = neighbor.weight
                
                if (visited.contains(nextNode)) continue

                activePathEdges.add(Pair(currNode, nextNode))

                steps.add(
                    VisualizationStep(
                        state = VisualizationState.GraphState(
                            nodes = nodes.map { it.copy() }, // In a real app we'd display distances on the nodes
                            edges = edges,
                            visitedNodes = visited.toSet(),
                            activePathEdges = activePathEdges.toSet(),
                            currentNode = currNode
                        ),
                        explanation = "Evaluating edge to node $nextNode with weight $weight.",
                        activeLine = 9
                    )
                )

                val newDist = currDist + weight
                val oldDist = distances[nextNode] ?: Int.MAX_VALUE

                if (newDist < oldDist) {
                    distances[nextNode] = newDist
                    priorityQueue.add(Pair(nextNode, newDist))

                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.GraphState(
                                nodes = nodes.map { it.copy() },
                                edges = edges,
                                visitedNodes = visited.toSet(),
                                activePathEdges = activePathEdges.toSet(),
                                currentNode = nextNode
                            ),
                            explanation = "Found a shorter path to $nextNode (Distance: $newDist). Updating and adding to queue.",
                            activeLine = 11
                        )
                    )
                } else {
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.GraphState(
                                nodes = nodes.map { it.copy() },
                                edges = edges,
                                visitedNodes = visited.toSet(),
                                activePathEdges = activePathEdges.toSet(),
                                currentNode = nextNode
                            ),
                            explanation = "Path to $nextNode (Distance: $newDist) is not shorter than the known path ($oldDist). Ignoring.",
                            activeLine = 10
                        )
                    )
                }

                activePathEdges.remove(Pair(currNode, nextNode))
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
                explanation = "Priority Queue is empty. Dijkstra's Shortest Path mapping is complete."
            )
        )

        return steps
    }
}
