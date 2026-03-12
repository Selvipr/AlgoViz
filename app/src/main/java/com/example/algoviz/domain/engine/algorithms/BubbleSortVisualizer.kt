package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.ArrayStep

class BubbleSortVisualizer : AlgorithmVisualizer {

    override fun visualize(initialArray: List<Int>): List<ArrayStep> {
        val steps = mutableListOf<ArrayStep>()
        val arr = initialArray.toMutableList()
        val n = arr.size
        val sortedIndices = mutableSetOf<Int>()

        // Initial state step
        steps.add(
            ArrayStep(
                array = arr.toList(),
                explanation = "Starting Bubble Sort on a ${n}-element array."
            )
        )

        for (i in 0 until n - 1) {
            var swappedThisPass = false
            for (j in 0 until n - i - 1) {
                // Step for comparison
                steps.add(
                    ArrayStep(
                        array = arr.toList(),
                        comparingIndices = listOf(j, j + 1),
                        sortedIndices = sortedIndices.toSet(),
                        explanation = "Comparing element at index $j (${arr[j]}) with index ${j + 1} (${arr[j + 1]})."
                    )
                )

                if (arr[j] > arr[j + 1]) {
                    // Swap
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    swappedThisPass = true

                    // Step for swap
                    steps.add(
                        ArrayStep(
                            array = arr.toList(),
                            comparingIndices = listOf(j, j + 1),
                            swapped = true,
                            sortedIndices = sortedIndices.toSet(),
                            explanation = "Since ${arr[j + 1]} > ${arr[j]}, we swap them."
                        )
                    )
                } else {
                    steps.add(
                        ArrayStep(
                            array = arr.toList(),
                            comparingIndices = listOf(j, j + 1),
                            swapped = false,
                            sortedIndices = sortedIndices.toSet(),
                            explanation = "Since ${arr[j]} <= ${arr[j + 1]}, no swap is needed."
                        )
                    )
                }
            }
            
            // At the end of the pass, the last evaluated element is in its final sorted position
            val finalPos = n - i - 1
            sortedIndices.add(finalPos)
            steps.add(
                ArrayStep(
                    array = arr.toList(),
                    sortedIndices = sortedIndices.toSet(),
                    explanation = "Pass complete. Element ${arr[finalPos]} is now in its final sorted position."
                )
            )
            
            // Optimization: if no swaps occurred, array is completely sorted
            if (!swappedThisPass) {
                // Mark all remaining elements as sorted
                for (k in 0 until finalPos) {
                    sortedIndices.add(k)
                }
                steps.add(
                    ArrayStep(
                        array = arr.toList(),
                        sortedIndices = sortedIndices.toSet(),
                        explanation = "No swaps occurred in this pass. The array is fully sorted early!"
                    )
                )
                break
            }
        }
        
        // Add the very first element (index 0) to sorted indices at the end if not already added
        sortedIndices.add(0)
        steps.add(
            ArrayStep(
                array = arr.toList(),
                sortedIndices = sortedIndices.toSet(),
                explanation = "Bubble Sort is complete! The array is fully sorted."
            )
        )

        return steps
    }
}
