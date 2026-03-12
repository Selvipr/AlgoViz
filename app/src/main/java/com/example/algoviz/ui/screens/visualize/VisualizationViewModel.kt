package com.example.algoviz.ui.screens.visualize

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.engine.AlgorithmVisualizer
import com.example.algoviz.domain.engine.ArrayStep
import com.example.algoviz.domain.engine.algorithms.BubbleSortVisualizer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VisualizationState(
    val steps: List<ArrayStep> = emptyList(),
    val currentStepIndex: Int = 0,
    val isPlaying: Boolean = false,
    val playbackSpeed: Float = 1.0f, // 0.25x to 4.0x
    val title: String = "",
    val isComplete: Boolean = false
) {
    val currentStep: ArrayStep?
        get() = steps.getOrNull(currentStepIndex)
}

class VisualizationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VisualizationState())
    val uiState: StateFlow<VisualizationState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private val baseDelayMs = 800L

    init {
        // For MVP, we'll initialize with Bubble Sort directly
        // In a full implementation, we'd pass the algoId and use a Factory to get the Visualizer
        loadAlgorithm("bubble_sort")
    }

    private fun loadAlgorithm(algoId: String) {
        val visualizer: AlgorithmVisualizer = when (algoId) {
            "bubble_sort" -> BubbleSortVisualizer()
            else -> BubbleSortVisualizer() // Default fallback
        }

        // Generate a random array of 10 elements for demonstration
        val initialArray = List(10) { (10..99).random() }
        val generatedSteps = visualizer.visualize(initialArray)

        _uiState.update { 
            it.copy(
                steps = generatedSteps,
                currentStepIndex = 0,
                isPlaying = false,
                title = "Bubble Sort",
                isComplete = false
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
                
                delay(delayTime)

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
}
