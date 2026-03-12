package com.example.algoviz.domain.engine

enum class ActionType {
    IDLE,
    COMPARE,
    SWAP,
    HIGHLIGHT,
    FOUND,
    NOT_FOUND,
    SOLVED
}

/**
 * Represents a single discrete step in an algorithm visualization.
 * 
 * @property state The structural state of the data (Array, Graph, Tree).
 * @property explanation A human-readable plain text explanation of what is happening in this step.
 * @property action The action classifying this step to drive specific UI animations.
 * @property activeLine The pseudo-code line to highlight for code-sync.
 */
data class VisualizationStep(
    val state: VisualizationState,
    val explanation: String = "",
    val action: ActionType = ActionType.IDLE,
    val activeLine: Int? = null
)

/**
 * Interface for all algorithm visualizers.
 */
interface AlgorithmVisualizer {
    /**
     * Generates a sequence of steps mapping the algorithm's execution based on an initial state.
     */
    fun visualize(initialData: Any): List<VisualizationStep>
}
