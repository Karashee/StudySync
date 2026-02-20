package com.example.assignmenttracker.presentation.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.domain.usecase.GetAllAssignmentsUseCase
import com.example.assignmenttracker.domain.usecase.UpdateAssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BoardUiState {
    object Loading : BoardUiState
    data class Success(
        val notStarted: List<Assignment>,
        val inProgress: List<Assignment>,
        val done: List<Assignment>
    ) : BoardUiState
    data class Error(val message: String) : BoardUiState
}

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val getAllAssignmentsUseCase: GetAllAssignmentsUseCase,
    private val updateAssignmentUseCase: UpdateAssignmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BoardUiState>(BoardUiState.Loading)
    val uiState: StateFlow<BoardUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                getAllAssignmentsUseCase().collect { assignments ->
                    _uiState.value = BoardUiState.Success(
                        notStarted = assignments.filter { it.status == Status.NOT_STARTED },
                        inProgress = assignments.filter { it.status == Status.IN_PROGRESS },
                        done = assignments.filter { it.status == Status.DONE }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = BoardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun moveToNextStatus(assignment: Assignment) {
        viewModelScope.launch {
            val newStatus = when (assignment.status) {
                Status.NOT_STARTED -> Status.IN_PROGRESS
                Status.IN_PROGRESS -> Status.DONE
                Status.DONE -> Status.NOT_STARTED
            }
            updateAssignmentUseCase(assignment.copy(status = newStatus))
        }
    }
}


