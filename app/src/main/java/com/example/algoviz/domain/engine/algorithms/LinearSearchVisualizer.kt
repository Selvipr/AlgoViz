package com.example.algoviz.domain.engine.algorithms

import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualNode
import com.example.algoviz.domain.engine.VisualizationState
import com.example.algoviz.domain.engine.VisualizationStep
import java.util.UUID

class LinearSearchVisualizer : AlgorithmVisualizer {

    @Suppress("UNCHECKED_CAST")
    override fun visualize(initialData: Any): List<VisualizationStep> {
        val payload = initialData as? Pair<List<Int>, Int>
            ?: throw IllegalArgumentException("Linear Search expects Pair<List<Int>, Int> where Int is the target.")

        val array = payload.first.map { VisualNode(UUID.randomUUID().toString(), it) }
        val target = payload.second
        val steps = mutableListOf<VisualizationStep>()

        steps.add(
            VisualizationStep(
                state = VisualizationState.ArrayState(array = array),
                explanation = "Initializing Linear Search to find target value: $target.",
                action = ActionType.IDLE,
                activeLine = 2
            )
        )

        var found = false
        for (i in array.indices) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(
                        array = array,
                        comparingIndices = listOf(i)
                    ),
                    explanation = "Checking index $i. Is ${array[i].value} equal to $target?",
                    action = ActionType.COMPARE,
                    activeLine = 3
                )
            )

            if (array[i].value == target) {
                steps.add(
                    VisualizationStep(
                        state = VisualizationState.ArrayState(
                            array = array,
                            sortedIndices = setOf(i) // Green highlight
                        ),
                        explanation = "Target $target found at index $i!",
                        action = ActionType.FOUND,
                        activeLine = 4
                    )
                )
                found = true
                break
            }
        }

        if (!found) {
            steps.add(
                VisualizationStep(
                    state = VisualizationState.ArrayState(array = array),
                    explanation = "Reached the end of the array. Target $target was not found.",
                    action = ActionType.NOT_FOUND,
                    activeLine = 5
                )
            )
        }

        return steps
    }
}
