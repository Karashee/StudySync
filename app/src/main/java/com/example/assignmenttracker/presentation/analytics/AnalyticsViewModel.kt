package com.example.assignmenttracker.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.domain.usecase.GetAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AnalyticsUiState {
    object Loading : AnalyticsUiState
    data class Success(
        val completedThisWeek: Int,
        val overdueCount: Int,
        val weeklyCompletionData: List<WeekData>
    ) : AnalyticsUiState
    data class Error(val message: String) : AnalyticsUiState
}

data class WeekData(
    val weekLabel: String,
    val completedCount: Int
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyticsUiState>(AnalyticsUiState.Loading)
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            try {
                getAnalyticsUseCase().collect { analytics ->
                    // Calculate weekly completion data for last 4 weeks
                    val weeklyData = calculateWeeklyData(analytics.completedThisWeek)
                    
                    _uiState.value = AnalyticsUiState.Success(
                        completedThisWeek = analytics.completedThisWeek,
                        overdueCount = analytics.overdueCount,
                        weeklyCompletionData = weeklyData
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AnalyticsUiState.Error(
                    e.message ?: "Failed to load analytics"
                )
            }
        }
    }

    private fun calculateWeeklyData(completedThisWeek: Int): List<WeekData> {
        // For simplicity, we'll show the current week's data
        // In a real app, you'd query historical data for past weeks
        return listOf(
            WeekData("Week 1", 0),
            WeekData("Week 2", 0),
            WeekData("Week 3", 0),
            WeekData("This Week", completedThisWeek)
        )
    }

    fun refresh() {
        _uiState.value = AnalyticsUiState.Loading
        loadAnalytics()
    }
}
