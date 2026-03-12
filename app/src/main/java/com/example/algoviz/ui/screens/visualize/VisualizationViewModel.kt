package com.example.algoviz.ui.screens.visualize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.engine.ActionType
import com.example.algoviz.domain.engine.AlgorithmDataProvider
import com.example.algoviz.domain.engine.AlgorithmInfo
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.VisualizationStep
import com.example.algoviz.domain.engine.algorithms.BFSVisualizer
import com.example.algoviz.domain.engine.algorithms.BSTInsertVisualizer
import com.example.algoviz.domain.engine.algorithms.BinarySearchVisualizer
import com.example.algoviz.domain.engine.algorithms.BubbleSortVisualizer
import com.example.algoviz.domain.engine.algorithms.DFSVisualizer
import com.example.algoviz.domain.engine.algorithms.DijkstraEdge
import com.example.algoviz.domain.engine.algorithms.DijkstraVisualizer
import com.example.algoviz.domain.engine.algorithms.HeapSortVisualizer
import com.example.algoviz.domain.engine.algorithms.LinearSearchVisualizer
import com.example.algoviz.domain.engine.algorithms.MergeSortVisualizer
import com.example.algoviz.domain.engine.algorithms.QuickSortVisualizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VisualizationState(
    val steps: List<VisualizationStep> = emptyList(),
    val currentStepIndex: Int = 0,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1.0f, // 0.25x to 4.0x
    val title: String = "",
    val isComplete: Boolean = false,
    val algorithmInfo: AlgorithmInfo? = null
) {
    val currentStep: VisualizationStep?
        get() = steps.getOrNull(currentStepIndex)
}

class VisualizationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VisualizationState())
    val uiState: StateFlow<VisualizationState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private val baseDelayMs = 800L

    private var isInitialized = false

    fun initialize(algoId: String) {
        if (isInitialized) return
        isInitialized = true
        loadAlgorithm(algoId)
    }

    private fun loadAlgorithm(algoId: String) {
        val (visualizer, initialData, title) = when (algoId) {
            "bfs" -> {
                val graphData = mapOf(1 to listOf(2, 3), 2 to listOf(1, 4, 5), 3 to listOf(1, 6), 4 to listOf(2), 5 to listOf(2, 6), 6 to listOf(3, 5))
                Triple(BFSVisualizer(), graphData, "Breadth-First Search")
            }
            "dfs" -> {
                val graphData = mapOf(1 to listOf(2, 3), 2 to listOf(1, 4, 5), 3 to listOf(1, 6), 4 to listOf(2), 5 to listOf(2, 6), 6 to listOf(3, 5))
                Triple(DFSVisualizer(), graphData, "Depth-First Search")
            }
            "dijkstra" -> {
                val graphData = mapOf(
                    1 to listOf(DijkstraEdge(2, 4), DijkstraEdge(3, 1)),
                    2 to listOf(DijkstraEdge(1, 4), DijkstraEdge(4, 2), DijkstraEdge(5, 5)),
                    3 to listOf(DijkstraEdge(1, 1), DijkstraEdge(6, 6)),
                    4 to listOf(DijkstraEdge(2, 2)),
                    5 to listOf(DijkstraEdge(2, 5), DijkstraEdge(6, 3)),
                    6 to listOf(DijkstraEdge(3, 6), DijkstraEdge(5, 3))
                )
                Triple(DijkstraVisualizer(), graphData, "Dijkstra's Shortest Path")
            }
            "bst_insert" -> {
                val dataToInsert = listOf(50, 30, 20, 40, 70, 60, 80)
                Triple(BSTInsertVisualizer(), dataToInsert, "BST Insert")
            }
            "bubble_sort" -> Triple(BubbleSortVisualizer(), List(10) { (10..99).random() }, "Bubble Sort")
            "merge_sort" -> Triple(MergeSortVisualizer(), List(10) { (10..99).random() }, "Merge Sort")
            "quick_sort" -> Triple(QuickSortVisualizer(), List(10) { (10..99).random() }, "Quick Sort")
            "heap_sort" -> Triple(HeapSortVisualizer(), List(10) { (10..99).random() }, "Heap Sort")
            "binary_search" -> {
                val sortedArray = List(10) { (10..99).random() }.sorted()
                val target = sortedArray.random()
                Triple(BinarySearchVisualizer(), Pair(sortedArray, target), "Binary Search")
            }
            "linear_search" -> {
                val unsortedArray = List(10) { (10..99).random() }
                val target = unsortedArray.random()
                Triple(LinearSearchVisualizer(), Pair(unsortedArray, target), "Linear Search")
            }
            else -> Triple(BubbleSortVisualizer(), List(10) { (10..99).random() }, "Bubble Sort (Fallback)")
        }

        val generatedSteps = visualizer.visualize(initialData)
        val info = AlgorithmDataProvider.algorithmInfoMap[algoId]

        _uiState.update { 
            it.copy(
                steps = generatedSteps,
                currentStepIndex = 0,
                isPlaying = false,
                title = title,
                isComplete = false,
                algorithmInfo = info
            )
        }
    }

    fun togglePlayPause() {
        val currentState = _uiState.value
        if (currentState.isComplete && !currentState.isPlaying) {
            // Restart from beginning if finished
            _uiState.update { it.copy(currentStepIndex = 0, isComplete = false) }
        }

        val willPlay = !currentState.isPlaying
        _uiState.update { it.copy(isPlaying = willPlay) }

        if (willPlay) {
            startPlayback()
        } else {
            stopPlayback()
        }
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (_uiState.value.isPlaying) {
                val state = _uiState.value
                val delayTime = (baseDelayMs / state.playbackSpeed).toLong()
                
                // Add micro-pauses based on the action being performed
                val adjustedDelay = when (state.currentStep?.action) {
                    ActionType.SWAP -> (delayTime * 1.5).toLong()
                    ActionType.HIGHLIGHT -> (delayTime * 1.5).toLong()
                    ActionType.SOLVED -> delayTime * 2
                    else -> delayTime
                }

                delay(adjustedDelay)

                if (state.currentStepIndex < state.steps.size - 1) {
                    _uiState.update { it.copy(currentStepIndex = it.currentStepIndex + 1) }
                } else {
                    _uiState.update { it.copy(isPlaying = false, isComplete = true) }
                    break
                }
            }
        }
    }

    private fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
    }

    fun stepForward() {
        stopPlayback()
        _uiState.update { state ->
            if (state.currentStepIndex < state.steps.size - 1) {
                state.copy(isPlaying = false, currentStepIndex = state.currentStepIndex + 1)
            } else {
                state.copy(isPlaying = false, isComplete = true)
            }
        }
    }

    fun stepBackward() {
        stopPlayback()
        _uiState.update { state ->
            if (state.currentStepIndex > 0) {
                state.copy(isPlaying = false, currentStepIndex = state.currentStepIndex - 1, isComplete = false)
            } else {
                state.copy(isPlaying = false)
            }
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
        // If already playing, restart loop to pick up new speed immediately
        if (_uiState.value.isPlaying) {
            startPlayback()
        }
    }

    fun scrubTo(stepIndex: Int) {
        stopPlayback()
        _uiState.update { state ->
            if (stepIndex in state.steps.indices) {
                state.copy(
                    currentStepIndex = stepIndex,
                    isComplete = stepIndex == state.steps.size - 1,
                    isPlaying = false
                )
            } else state
        }
    }
}
