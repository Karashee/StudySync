package com.example.assignmenttracker.data.preferences

data class UserPreferences(
    val userName: String = "",
    val course: String = "",
    val academicYear: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val hasCompletedOnboarding: Boolean = false
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
