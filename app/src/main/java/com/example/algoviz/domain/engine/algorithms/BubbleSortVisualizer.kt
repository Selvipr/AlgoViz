package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class BubbleSortVisualizer : AlgorithmVisualizer {

    override fun visualize(initialData: Any): List<VisualizationStep> {
        val initialArray = initialData as? List<Int> ?: throw IllegalArgumentException("BubbleSort requires List<Int>")
        val steps = mutableListOf<VisualizationStep>()
        val arr = initialArray.map { VisualNode(UUID.randomUUID().toString(), it) }.toMutableList()
        val n = arr.size
        val sortedIndices = mutableSetOf<Int>()

        // Initial state step
        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = arr.toList()),
                explanation = "Starting Bubble Sort on a ${n}-element array.",
                action = ActionType.IDLE,
                activeLine = 1 // Assuming 1 is function def
            )
        )

        for (i in 0 until n - 1) {
            var swappedThisPass = false
            for (j in 0 until n - i - 1) {
                // Step for comparison
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = arr.toList(),
                            comparingIndices = listOf(j, j + 1),
                            sortedIndices = sortedIndices.toSet()
                        ),
                        explanation = "Comparing element at index $j (${arr[j].value}) with index ${j + 1} (${arr[j + 1].value}).",
                        action = ActionType.COMPARE,
                        activeLine = 3 // Assuming 3 is interior loop condition / comparison
                    )
                )

                if (arr[j].value > arr[j + 1].value) {
                    // Swap
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    swappedThisPass = true

                    // Step for swap
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.ArrayState(
                                array = arr.toList(),
                                comparingIndices = listOf(j, j + 1),
                                swapped = true,
                                sortedIndices = sortedIndices.toSet()
                            ),
                            explanation = "Since ${arr[j + 1].value} > ${arr[j].value}, we swap them.",
                            action = ActionType.SWAP,
                            activeLine = 4 // Assuming 4 is the swap operation
                        )
                    )
                } else {
                    steps.add(
                        VisualizationStep(
                            state = VisualizationState.ArrayState(
                                array = arr.toList(),
                                comparingIndices = listOf(j, j + 1),
                                swapped = false,
                                sortedIndices = sortedIndices.toSet()
                            ),
                            explanation = "Since ${arr[j].value} <= ${arr[j + 1].value}, no swap is needed.",
                            action = ActionType.IDLE,
                            activeLine = 5
                        )
                    )
                }
            }
            
            // At the end of the pass, the last evaluated element is in its final sorted position
            val finalPos = n - i - 1
            sortedIndices.add(finalPos)
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = arr.toList(),
                        sortedIndices = sortedIndices.toSet()
                    ),
                    explanation = "Pass complete. Element ${arr[finalPos].value} is now in its final sorted position.",
                    action = ActionType.HIGHLIGHT,
                    activeLine = 2 // Outer loop increment
                )
            )
            
            // Optimization: if no swaps occurred, array is completely sorted
            if (!swappedThisPass) {
                // Mark all remaining elements as sorted
                for (k in 0 until finalPos) {
                    sortedIndices.add(k)
                }
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = arr.toList(),
                            sortedIndices = sortedIndices.toSet()
                        ),
                        explanation = "No swaps occurred in this pass. The array is fully sorted early!",
                        action = ActionType.SOLVED,
                        activeLine = 8 // break
                    )
                )
                break
            }
        }
        
        // Add the very first element (index 0) to sorted indices at the end if not already added
        sortedIndices.add(0)
        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(
                    array = arr.toList(),
                    sortedIndices = sortedIndices.toSet()
                ),
                explanation = "Bubble Sort is complete! The array is fully sorted.",
                action = ActionType.SOLVED,
                activeLine = 9 // return
            )
        )

        return steps
    }
}
