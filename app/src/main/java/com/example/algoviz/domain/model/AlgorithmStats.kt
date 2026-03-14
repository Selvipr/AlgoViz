package com.example.algoviz.domain.model

data class AlgorithmStats(
    val id: String,
    val name: String,
    val bestTime: String,
    val averageTime: String,
    val worstTime: String,
    val spaceComplexity: String,
    val isStable: Boolean,
    val description: String
)
