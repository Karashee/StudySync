package com.example.assignmenttracker.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.usecase.GetAllAssignmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

sealed interface CalendarUiState {
    object Loading : CalendarUiState
    data class Success(
        val month: YearMonth,
        val assignmentsByDate: Map<LocalDate, List<Assignment>>
    ) : CalendarUiState
    data class Error(val message: String) : CalendarUiState
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getAllAssignmentsUseCase: GetAllAssignmentsUseCase
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                getAllAssignmentsUseCase().collect { assignments ->
                    val assignmentsByDate = assignments.groupBy { it.dueDate }
                    _uiState.value = CalendarUiState.Success(
                        month = _selectedMonth.value,
                        assignmentsByDate = assignmentsByDate
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun navigateToPreviousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }

    fun navigateToNextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }
}

