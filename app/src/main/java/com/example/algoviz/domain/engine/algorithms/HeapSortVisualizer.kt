package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class HeapSortVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val array = initialData as? List<Int>
            ?: throw IllegalArgumentException("Heap Sort expects List<Int>")

        val steps = mutableListOf<VisualizationStep>()
        val arr = array.map { VisualNode(UUID.randomUUID().toString(), it) }.toTypedArray()
        val n = arr.size

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = arr.toList()),
                explanation = "Initializing Heap Sort. Step 1: Build a Max Heap.",
                action = ActionType.IDLE,
                activeLine = 2
            )
        )

        // Build max heap
        for (i in n / 2 - 1 downTo 0) {
            heapify(arr, n, i, steps)
        }

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = arr.toList()),
                explanation = "Max Heap built successfully. The largest element is now at the root (index 0).",
                action = ActionType.HIGHLIGHT,
                activeLine = 3
            )
        )

        // Extract elements from heap one by one
        val sortedSet = mutableSetOf<Int>()
        for (i in n - 1 downTo 1) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(0, i),
                        sortedIndices = sortedSet.toSet()
                    ),
                    explanation = "Swapping the root element ${arr[0].value} (max) with the last element ${arr[i].value}.",
                    action = ActionType.SWAP,
                    activeLine = 4
                )
            )

            // Swap current root with end
            val temp = arr[0]
            arr[0] = arr[i]
            arr[i] = temp
            
            sortedSet.add(i)

            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(0, i),
                        swapped = true,
                        sortedIndices = sortedSet.toSet()
                    ),
                    explanation = "${arr[i].value} is now in its sorted position at the end of the reduced heap.",
                    action = ActionType.HIGHLIGHT,
                    activeLine = 5
                )
            )

            // Call max heapify on the reduced heap
            heapify(arr, i, 0, steps, sortedSet)
        }
        
        sortedSet.add(0)

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    sortedIndices = sortedSet.toSet()
                ),
                explanation = "Heap Sort is complete. The array is fully sorted.",
                action = ActionType.SOLVED
            )
        )

        return steps
    }

    private fun heapify(arr: Array<VisualNode>, n: Int, i: Int, steps: MutableList<VisualizationStep>, sortedSet: Set<Int> = emptySet()) {
        var largest = i
        val left = 2 * i + 1
        val right = 2 * i + 2

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    comparingIndices = listOf(i),
                    sortedIndices = sortedSet
                ),
                explanation = "Heapifying at root node index $i (${arr[i].value}).",
                action = ActionType.COMPARE,
                activeLine = 12
            )
        )

        if (left < n && arr[left].value > arr[largest].value) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(largest, left),
                        sortedIndices = sortedSet
                    ),
                    explanation = "Left child ${arr[left].value} is greater than current largest ${arr[largest].value}.",
                    action = ActionType.COMPARE,
                    activeLine = 16
                )
            )
            largest = left
        }

        if (right < n && arr[right].value > arr[largest].value) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(largest, right),
                        sortedIndices = sortedSet
                    ),
                    explanation = "Right child ${arr[right].value} is greater than current largest ${arr[largest].value}.",
                    action = ActionType.COMPARE,
                    activeLine = 18
                )
            )
            largest = right
        }

        if (largest != i) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(i, largest),
                        swapped = true,
                        sortedIndices = sortedSet
                    ),
                    explanation = "Swapping root ${arr[i].value} with the largest child ${arr[largest].value}.",
                    action = ActionType.SWAP,
                    activeLine = 20
                )
            )

            val swap = arr[i]
            arr[i] = arr[largest]
            arr[largest] = swap

            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest, steps, sortedSet)
        }
    }
}
