package com.example.assignmenttracker.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.data.preferences.UserPreferencesRepository
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.domain.usecase.DeleteAssignmentUseCase
import com.example.assignmenttracker.domain.usecase.SearchAssignmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

sealed interface AssignmentListUiState {
    object Loading : AssignmentListUiState
    data class Success(val assignments: List<Assignment>) : AssignmentListUiState
    data class Error(val message: String) : AssignmentListUiState
}

@HiltViewModel
class AssignmentListViewModel @Inject constructor(
    private val searchAssignmentsUseCase: SearchAssignmentsUseCase,
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Search & filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _statusFilter = MutableStateFlow<Status?>(null)
    val statusFilter: StateFlow<Status?> = _statusFilter

    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    val priorityFilter: StateFlow<Priority?> = _priorityFilter

    // UI State
    private val _uiState = MutableStateFlow<AssignmentListUiState>(AssignmentListUiState.Loading)
    val uiState: StateFlow<AssignmentListUiState> = _uiState

    // User name for greeting
    val userName: StateFlow<String> = userPreferencesRepository.userPreferencesFlow
        .map { it.userName }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    init {
        // Combine search query + filters to fetch assignments reactively
        combine(_searchQuery, _statusFilter, _priorityFilter) { query, status, priority ->
            Triple(query, status, priority)
        }
            .debounce(200)
            .flatMapLatest { (query, status, priority) ->
                searchAssignmentsUseCase(query, status, priority)
            }
            .onEach { assignments ->
                _uiState.value = AssignmentListUiState.Success(assignments)
            }
            .catch { e ->
                _uiState.value = AssignmentListUiState.Error(e.message ?: "Unknown error")
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateStatusFilter(status: Status?) {
        _statusFilter.value = status
    }

    fun updatePriorityFilter(priority: Priority?) {
        _priorityFilter.value = priority
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            deleteAssignmentUseCase(assignment)
        }
    }
}
