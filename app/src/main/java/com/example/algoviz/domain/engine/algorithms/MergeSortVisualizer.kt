package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class MergeSortVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val array = initialData as? List<Int>
            ?: throw IllegalArgumentException("Merge Sort expects List<Int>")

        val steps = mutableListOf<VisualizationStep>()
        val arr = array.map { VisualNode(UUID.randomUUID().toString(), it) }.toTypedArray()
        
        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = arr.toList()),
                explanation = "Initializing Merge Sort with an array of ${arr.size} elements.",
                action = ActionType.IDLE
            )
        )

        mergeSort(arr, 0, arr.size - 1, steps)

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    sortedIndices = arr.indices.toSet()
                ),
                explanation = "Merge Sort is complete. The array is fully sorted.",
                action = ActionType.SOLVED
            )
        )

        return steps
    }

    private fun mergeSort(arr: Array<VisualNode>, left: Int, right: Int, steps: MutableList<VisualizationStep>) {
        if (left < right) {
            val middle = left + (right - left) / 2

            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = (left..right).toList()
                    ),
                    explanation = "Dividing the subarray from index $left to $right at midpoint $middle.",
                    action = ActionType.HIGHLIGHT
                )
            )

            mergeSort(arr, left, middle, steps)
            mergeSort(arr, middle + 1, right, steps)
            merge(arr, left, middle, right, steps)
        }
    }

    private fun merge(arr: Array<VisualNode>, left: Int, middle: Int, right: Int, steps: MutableList<VisualizationStep>) {
        val n1 = middle - left + 1
        val n2 = right - middle

        val leftArr = Array(n1) { arr[left + it] }
        val rightArr = Array(n2) { arr[middle + 1 + it] }

        var i = 0
        var j = 0
        var k = left

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    comparingIndices = (left..right).toList()
                ),
                explanation = "Merging two sorted subarrays: ${leftArr.map { it.value }} and ${rightArr.map { it.value }} back into the main array from index $left to $right.",
                action = ActionType.HIGHLIGHT
            )
        )

        while (i < n1 && j < n2) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(left + i, middle + 1 + j)
                    ),
                    explanation = "Comparing left subarray element ${leftArr[i].value} with right subarray element ${rightArr[j].value}.",
                    action = ActionType.COMPARE
                )
            )

            if (leftArr[i].value <= rightArr[j].value) {
                arr[k] = leftArr[i]
                i++
            } else {
                arr[k] = rightArr[j]
                j++
            }
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(k),
                        swapped = true
                    ),
                    explanation = "Placed ${arr[k].value} at index $k.",
                    action = ActionType.SWAP
                )
            )
            k++
        }

        while (i < n1) {
            arr[k] = leftArr[i]
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(k)
                    ),
                    explanation = "Copying remaining element ${arr[k].value} from the left subarray.",
                    action = ActionType.SWAP
                )
            )
            i++
            k++
        }

        while (j < n2) {
            arr[k] = rightArr[j]
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        comparingIndices = listOf(k)
                    ),
                    explanation = "Copying remaining element ${arr[k].value} from the right subarray.",
                    action = ActionType.SWAP
                )
            )
            j++
            k++
        }
    }
}
