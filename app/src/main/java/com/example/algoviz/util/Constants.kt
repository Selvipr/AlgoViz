package com.example.algoviz.util

object Constants {
    // Supabase
    const val SUPABASE_URL = "https://dwotarbkkhcjrvoosnsn.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR3b3RhcmJra2hjanJ2b29zbnNuIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MjAzNDQzMiwiZXhwIjoyMDg3NjEwNDMyfQ.RxzSG9zCwNDHVGY8_bBuObQZMiM9E_y8mtZunONeZtM"

    // Tiers
    val TIER_ORDER = listOf("novice", "learner", "practitioner", "expert", "master", "grandmaster")
    val TIER_XP_THRESHOLDS = mapOf(
        "novice" to 0,
        "learner" to 500,
        "practitioner" to 2000,
        "expert" to 5000,
        "master" to 15000,
        "grandmaster" to 50000
    )

    // Difficulty
    const val DIFFICULTY_EASY = "easy"
    const val DIFFICULTY_MEDIUM = "medium"
    const val DIFFICULTY_HARD = "hard"

    // Verdict
    const val VERDICT_PENDING = "pending"
    const val VERDICT_ACCEPTED = "accepted"
    const val VERDICT_WRONG_ANSWER = "wrong_answer"
    const val VERDICT_TLE = "time_limit_exceeded"
    const val VERDICT_RUNTIME_ERROR = "runtime_error"
    const val VERDICT_COMPILATION_ERROR = "compilation_error"

    // Daily XP defaults
    const val DEFAULT_DAILY_XP_GOAL = 50
    const val XP_VISUALIZATION_WATCHED = 10
    const val XP_LESSON_COMPLETED = 25
    const val XP_PROBLEM_EASY = 10
    const val XP_PROBLEM_MEDIUM = 25
    const val XP_PROBLEM_HARD = 50
    const val XP_QUIZ_PERFECT = 15
    const val XP_STREAK_BONUS = 5
}
