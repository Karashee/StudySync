package com.example.assignmenttracker.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    fun completeOnboarding(
        userName: String,
        course: String,
        academicYear: String
    ) {
        viewModelScope.launch {
            userPreferencesRepository.updateUserPreferences(
                userName = userName,
                course = course,
                academicYear = academicYear,
                hasCompletedOnboarding = true
            )
        }
    }
}
