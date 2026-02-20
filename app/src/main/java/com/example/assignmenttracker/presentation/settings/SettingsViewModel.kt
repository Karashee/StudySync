package com.example.assignmenttracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.preferences.ThemeMode
import com.example.assignmenttracker.data.preferences.UserPreferences
import com.example.assignmenttracker.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateUserName(name: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(userName = name)
        }
    }

    fun updateCourse(course: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(course = course)
        }
    }

    fun updateAcademicYear(year: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(academicYear = year)
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(themeMode = themeMode)
        }
    }
}
