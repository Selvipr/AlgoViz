package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class BinarySearchVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val payload = initialData as? Pair<List<Int>, Int>
            ?: throw IllegalArgumentException("Binary Search expects Pair<List<Int>, Int> where Int is the target.")

        val array = payload.first.sorted().map { VisualNode(UUID.randomUUID().toString(), it) } // Binary search requires a sorted array
        val target = payload.second
        val steps = mutableListOf<VisualizationStep>()

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = array),
                explanation = "Initializing Binary Search for target $target. Array must be sorted.",
                action = ActionType.IDLE,
                activeLine = 2
            )
        )

        var left = 0
        var right = array.size - 1
        var found = false

        while (left <= right) {
            val mid = left + (right - left) / 2

            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = array,
                        comparingIndices = listOf(mid),
                        sortedIndices = setOf(left, right) // Just reusing sortedIndices color as bounds pointers
                    ),
                    explanation = "Calculating midpoint at index $mid (${array[mid].value}). Left bound is $left, right bound is $right.",
                    action = ActionType.COMPARE,
                    activeLine = 6
                )
            )

            if (array[mid].value == target) {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = array,
                            sortedIndices = setOf(mid) // Highlight found element green
                        ),
                        explanation = "Target $target found at index $mid!",
                        action = ActionType.FOUND,
                        activeLine = 7
                    )
                )
                found = true
                break
            }

            if (array[mid].value < target) {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = array,
                            comparingIndices = listOf(mid)
                        ),
                        explanation = "${array[mid].value} is less than $target. The target must be in the right half.",
                        action = ActionType.IDLE,
                        activeLine = 9
                    )
                )
                left = mid + 1
            } else {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = array,
                            comparingIndices = listOf(mid)
                        ),
                        explanation = "${array[mid].value} is greater than $target. The target must be in the left half.",
                        action = ActionType.IDLE,
                        activeLine = 11
                    )
                )
                right = mid - 1
            }
        }

        if (!found) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(array = array),
                    explanation = "The bounds crossed. Target $target does not exist in the array.",
                    action = ActionType.NOT_FOUND,
                    activeLine = 12
                )
            )
        }

        return steps
    }
}
