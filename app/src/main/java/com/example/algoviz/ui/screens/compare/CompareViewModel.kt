package com.example.algoviz.ui.screens.compare

import androidx.lifecycle.ViewModel
import com.example.algoviz.domain.model.AlgorithmStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor() : ViewModel() {

    // Hardcoded repository of recognizable Algorithm models for visual comparison
    val availableAlgorithms = listOf(
        AlgorithmStats(
            id = "bubble_sort",
            name = "Bubble Sort",
            bestTime = "O(n)",
            averageTime = "O(n²)",
            worstTime = "O(n²)",
            spaceComplexity = "O(1)",
            isStable = true,
            description = "A simple comparison-based sorting that continuously steps through the list, swapping adjacent elements if they are in the wrong order."
        ),
        AlgorithmStats(
            id = "merge_sort",
            name = "Merge Sort",
            bestTime = "O(n log n)",
            averageTime = "O(n log n)",
            worstTime = "O(n log n)",
            spaceComplexity = "O(n)",
            isStable = true,
            description = "A divide-and-conquer algorithm that divides the array into halves, sorts them, and merges them continuously."
        ),
        AlgorithmStats(
            id = "quick_sort",
            name = "Quick Sort",
            bestTime = "O(n log n)",
            averageTime = "O(n log n)",
            worstTime = "O(n²)",
            spaceComplexity = "O(log n)",
            isStable = false,
            description = "Picks a pivot element and partitions the array into sub-arrays containing elements less than and greater than the pivot."
        ),
        AlgorithmStats(
            id = "dijkstra",
            name = "Dijkstra's",
            bestTime = "O((V+E) log V)",
            averageTime = "O((V+E) log V)",
            worstTime = "O((V+E) log V)",
            spaceComplexity = "O(V)",
            isStable = false,
            description = "Finds the shortest paths between nodes in a graph, which may represent, for example, road networks."
        ),
        AlgorithmStats(
            id = "a_star",
            name = "A* Search",
            bestTime = "O(E)",
            averageTime = "O(E)",
            worstTime = "O(b^d)",
            spaceComplexity = "O(b^d)",
            isStable = false,
            description = "An informed search algorithm that aims to find a path to the given goal node having the smallest cost using heuristics."
        )
    )

    private val _leftAlgorithm = MutableStateFlow(availableAlgorithms[0])
    val leftAlgorithm: StateFlow<AlgorithmStats> = _leftAlgorithm.asStateFlow()

    private val _rightAlgorithm = MutableStateFlow(availableAlgorithms[1])
    val rightAlgorithm: StateFlow<AlgorithmStats> = _rightAlgorithm.asStateFlow()

    fun setLeftAlgorithm(algo: AlgorithmStats) {
        _leftAlgorithm.value = algo
    }

    fun setRightAlgorithm(algo: AlgorithmStats) {
        _rightAlgorithm.value = algo
    }
}
