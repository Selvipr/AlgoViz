package com.example.algoviz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val college: String? = null,
    val location: String? = null,
    val githubUrl: String? = null,
    val linkedinUrl: String? = null,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val xp: Int = 0,
    val tier: String = "novice",
    val sarvamLanguage: String = "en",
    val uiLanguage: String = "en",
    val isPremium: Boolean = false,
    val dailyXpGoal: Int = 50,
    val visualizationSpeed: Float = 1.0f,
)

@Serializable
data class Topic(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val description: String? = null,
    val difficulty: String = "beginner",
    val orderIndex: Int = 0,
    val isPremium: Boolean = false,
    val iconUrl: String? = null,
    val estimatedTimeMinutes: Int = 30,
    val track: String? = null,
)

@Serializable
data class Problem(
    val id: String = "",
    val topicId: String? = null,
    val title: String = "",
    val difficulty: String = "easy",
    val description: String = "",
    val constraintsText: String? = null,
    val inputFormat: String? = null,
    val outputFormat: String? = null,
    val isPremium: Boolean = false,
    val isDailyChallenge: Boolean = false,
)

@Serializable
data class Submission(
    val id: String = "",
    val userId: String = "",
    val problemId: String = "",
    val code: String = "",
    val language: String = "",
    val verdict: String = "pending",
    val runtimeMs: Int? = null,
    val memoryKb: Int? = null,
)

@Serializable
data class Progress(
    val id: String = "",
    val userId: String = "",
    val topicId: String = "",
    val lessonCompleted: Boolean = false,
    val visualizationWatched: Boolean = false,
    val problemsSolved: Int = 0,
    val quizScore: Float? = null,
    val score: Int = 0,
)

@Serializable
data class Achievement(
    val id: String = "",
    val userId: String = "",
    val badgeId: String = "",
    val badgeName: String = "",
    val badgeDescription: String? = null,
    val badgeIcon: String? = null,
)
