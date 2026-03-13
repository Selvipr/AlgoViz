package com.example.algoviz.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")
    data object Signup : Screen("signup")

    // Main tabs
    data object Home : Screen("home")
    data object Learn : Screen("learn")
    data object Visualize : Screen("visualize")
    data object Arena : Screen("arena")
    data object Profile : Screen("profile")

    // Detail screens
    data object TopicDetail : Screen("topic/{topicId}") {
        fun createRoute(topicId: String) = "topic/$topicId"
    }
    data object LessonDetail : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    data object VisualizationPlayer : Screen("visualization/{visualizationId}") {
        fun createRoute(visualizationId: String) = "visualization/$visualizationId"
    }
    data object ProblemDetail : Screen("problem/{problemId}") {
        fun createRoute(problemId: String) = "problem/$problemId"
    }
    data object CodeEditor : Screen("editor/{problemId}") {
        fun createRoute(problemId: String) = "editor/$problemId"
    }
    data object AIChat : Screen("ai_chat")
    data object Settings : Screen("settings")
    data object Leaderboard : Screen("leaderboard")
    data object Browser : Screen("browser")
}
