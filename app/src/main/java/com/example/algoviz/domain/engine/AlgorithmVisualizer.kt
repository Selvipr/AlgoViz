package com.example.algoviz.domain.engine

/**
 * Represents a single discrete step in an array-based algorithm visualization.
 * 
 * @property array The current state of the array at this step.
 * @property comparingIndices The indices currently being compared (highlighted).
 * @property swapped Whether the elements at comparingIndices were just swapped.
 * @property sortedIndices The set of indices that are in their final sorted position.
 * @property explanation A human-readable plain text explanation of what is happening in this step.
 */
data class ArrayStep(
    val array: List<Int>,
    val comparingIndices: List<Int> = emptyList(),
    val swapped: Boolean = false,
    val sortedIndices: Set<Int> = emptySet(),
    val explanation: String = ""
)

/**
 * Interface for all array-based algorithm visualizers.
 */
interface AlgorithmVisualizer {
    /**
     * Given an initial unsorted array, generates a sequence of steps mapping the algorithm's execution.
     */
    fun visualize(initialArray: List<Int>): List<ArrayStep>
}
