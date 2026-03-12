package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class QuickSortVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val array = initialData as? List<Int>
            ?: throw IllegalArgumentException("Quick Sort expects List<Int>")

        val steps = mutableListOf<VisualizationStep>()
        val arr = array.map { VisualNode(UUID.randomUUID().toString(), it) }.toTypedArray()

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = arr.toList()),
                explanation = "Initializing Quick Sort.",
                action = ActionType.IDLE
            )
        )

        quickSort(arr, 0, arr.size - 1, steps)

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    sortedIndices = arr.indices.toSet()
                ),
                explanation = "Quick Sort is complete. The array is fully sorted.",
                action = ActionType.SOLVED
            )
        )

        return steps
    }

    private fun quickSort(arr: Array<VisualNode>, low: Int, high: Int, steps: MutableList<VisualizationStep>) {
        if (low < high) {
            val pivotIndex = partition(arr, low, high, steps)
            
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        sortedIndices = setOf(pivotIndex)
                    ),
                    explanation = "Pivot ${arr[pivotIndex].value} is now in its correct sorted position.",
                    action = ActionType.HIGHLIGHT
                )
            )

            quickSort(arr, low, pivotIndex - 1, steps)
            quickSort(arr, pivotIndex + 1, high, steps)
        }
    }

    private fun partition(arr: Array<VisualNode>, low: Int, high: Int, steps: MutableList<VisualizationStep>): Int {
        val pivotNode = arr[high]
        var i = low - 1

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    comparingIndices = listOf(high)
                ),
                explanation = "Selecting ${pivotNode.value} as the pivot element.",
                action = ActionType.COMPARE
            )
        )

        for (j in low until high) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(j, high)
                    ),
                    explanation = "Comparing ${arr[j].value} with pivot ${pivotNode.value}.",
                    action = ActionType.COMPARE
                )
            )

            if (arr[j].value < pivotNode.value) {
                i++
                if (i != j) {
                    val temp = arr[i]
                    arr[i] = arr[j]
                    arr[j] = temp
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.ArrayState(
                                array = arr.toList(),
                                comparingIndices = listOf(i, j),
                                swapped = true
                            ),
                            explanation = "${arr[j].value} is less than ${pivotNode.value}. Swapping with the element at index $i.",
                            action = ActionType.SWAP
                        )
                    )
                }
            }
        }

        val temp = arr[i + 1]
        arr[i + 1] = arr[high]
        arr[high] = temp

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    comparingIndices = listOf(i + 1, high),
                    swapped = true
                ),
                explanation = "Moving the pivot ${pivotNode.value} into its final place by swapping with ${arr[high].value}.",
                action = ActionType.SWAP
            )
        )

        return i + 1
    }
}
