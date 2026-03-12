package com.example.algoviz.domain.engine

data class AlgorithmInfo(
    val title: String,
    val description: String,
    val steps: List<String>,
    val timeComplexity: String,
    val spaceComplexity: String,
    val useCases: String,
    val youtubeVideoId: String,
    val pseudoCode: String = ""
)
