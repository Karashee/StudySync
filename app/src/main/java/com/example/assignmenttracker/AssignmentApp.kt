package com.example.assignmenttracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.assignmenttracker.presentation.navigation.AssignmentNavGraph
import com.example.assignmenttracker.presentation.settings.SettingsViewModel
import com.example.assignmenttracker.presentation.theme.AssignmentTrackerTheme

@Composable
fun AssignmentApp(
    initialAssignmentId: Long? = null,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    
    AssignmentTrackerTheme(
        darkTheme = when (preferences.themeMode) {
            com.example.assignmenttracker.data.preferences.ThemeMode.LIGHT -> false
            com.example.assignmenttracker.data.preferences.ThemeMode.DARK -> true
            com.example.assignmenttracker.data.preferences.ThemeMode.SYSTEM -> 
                androidx.compose.foundation.isSystemInDarkTheme()
        }
    ) {
        AssignmentNavGraph(
            navController = navController,
            startDestination = if (preferences.hasCompletedOnboarding) "list" else "onboarding"
        )
        
        // Navigate to assignment detail if opened from notification
        LaunchedEffect(initialAssignmentId) {
            initialAssignmentId?.let { id ->
                if (preferences.hasCompletedOnboarding) {
                    navController.navigate("detail/$id")
                }
            }
        }
    }
}
