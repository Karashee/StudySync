package com.example.assignmenttracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.assignmenttracker.presentation.analytics.AnalyticsScreen
import com.example.assignmenttracker.presentation.board.BoardScreen
import com.example.assignmenttracker.presentation.calendar.CalendarScreen
import com.example.assignmenttracker.presentation.detail.AssignmentDetailScreen
import com.example.assignmenttracker.presentation.list.AssignmentListScreen
import com.example.assignmenttracker.presentation.onboarding.OnboardingScreen
import com.example.assignmenttracker.presentation.settings.SettingsScreen

@Composable
fun AssignmentNavGraph(
    navController: NavHostController,
    startDestination: String = "list",
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // List Screen
        composable("list") {
            AssignmentListScreen(
                onNavigateToDetail = { id: Long ->
                    navController.navigate("detail/$id")
                },
                onNavigateToAdd = {
                    navController.navigate("detail/new")
                },
                onNavigateToCalendar = {
                    navController.navigate("calendar")
                },
                onNavigateToBoard = {
                    navController.navigate("board")
                }
            )
        }

        // Detail Screen – FIXED
        composable(
            route = "detail/{assignmentId}",
            arguments = listOf(
                navArgument("assignmentId") {
                    type = NavType.StringType       // accepts "new" or "23"
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val arg = backStackEntry.arguments?.getString("assignmentId")
            val assignmentId: Long? = arg?.toLongOrNull()  // null when "new"

            AssignmentDetailScreen(
                assignmentId = assignmentId, // null ⇒ NEW MODE
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Calendar Screen
        composable("calendar") {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id: Long ->
                    navController.navigate("detail/$id")
                }
            )
        }

        // Board Screen
        composable("board") {
            BoardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id: Long ->
                    navController.navigate("detail/$id")
                }
            )
        }

        // Analytics
        composable("analytics") {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Onboarding
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("list") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
    }
}
