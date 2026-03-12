package com.example.algoviz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.algoviz.ui.screens.arena.ArenaScreen
import com.example.algoviz.ui.screens.auth.LoginScreen
import com.example.algoviz.ui.screens.auth.SignupScreen
import com.example.algoviz.ui.screens.home.HomeScreen
import com.example.algoviz.ui.screens.learn.LearnScreen
import com.example.algoviz.ui.screens.learn.LearnDetailScreen
import com.example.algoviz.ui.screens.profile.ProfileScreen
import com.example.algoviz.ui.screens.settings.SettingsScreen
import com.example.algoviz.ui.screens.visualize.VisualizeScreen
import com.example.algoviz.ui.screens.visualize.VisualizationPlayerScreen
import com.example.algoviz.ui.screens.arena.ArenaDetailScreen

@Composable
fun AlgoVizNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignupSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main tabs
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTopic = { topicId ->
                    navController.navigate(Screen.TopicDetail.createRoute(topicId))
                },
                onNavigateToArena = {
                    navController.navigate(Screen.Arena.route)
                },
                onNavigateToProblem = { problemId ->
                    navController.navigate(Screen.ProblemDetail.createRoute(problemId))
                },
                onNavigateToVisualize = { vizId ->
                    navController.navigate(Screen.VisualizationPlayer.createRoute(vizId))
                }
            )
        }
        composable(Screen.Learn.route) {
            LearnScreen(
                onNavigateToTopic = { topicId ->
                    navController.navigate(Screen.TopicDetail.createRoute(topicId))
                }
            )
        }
        composable(Screen.TopicDetail.route) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            LearnDetailScreen(
                topicId = topicId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVisualize = { vizId ->
                    navController.navigate(Screen.VisualizationPlayer.createRoute(vizId))
                }
            )
        }
        composable(Screen.Visualize.route) {
            VisualizeScreen(
                onNavigateToVisualization = { vizId ->
                    navController.navigate(Screen.VisualizationPlayer.createRoute(vizId))
                }
            )
        }
        composable(Screen.VisualizationPlayer.route) { backStackEntry ->
            val vizId = backStackEntry.arguments?.getString("visualizationId") ?: ""
            VisualizationPlayerScreen(
                vizId = vizId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Arena.route) {
            ArenaScreen(
                onNavigateToProblem = { problemId ->
                    navController.navigate(Screen.ProblemDetail.createRoute(problemId))
                }
            )
        }
        composable(Screen.ProblemDetail.route) { backStackEntry ->
            val problemId = backStackEntry.arguments?.getString("problemId") ?: ""
            ArenaDetailScreen(
                problemId = problemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }
    }
}
