package com.example.assignmenttracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.domain.usecase.CreateAssignmentUseCase
import com.example.assignmenttracker.domain.usecase.GetAssignmentUseCase
import com.example.assignmenttracker.domain.usecase.UpdateAssignmentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class AssignmentDetailUiState(
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate = LocalDate.now(),
    val dueTime: LocalTime = LocalTime.now(),
    val priority: Priority = Priority.MEDIUM,
    val status: Status = Status.NOT_STARTED,
    val titleError: String? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class AssignmentDetailViewModel @Inject constructor(
    private val getAssignmentUseCase: GetAssignmentUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val updateAssignmentUseCase: UpdateAssignmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val assignmentId: Long? = savedStateHandle.get<String>("assignmentId")?.toLongOrNull()
    
    private val _uiState = MutableStateFlow(AssignmentDetailUiState())
    val uiState: StateFlow<AssignmentDetailUiState> = _uiState.asStateFlow()
    
    init {
        assignmentId?.let { loadAssignment(it) }
    }
    
    private fun loadAssignment(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val assignment = getAssignmentUseCase(id)
            assignment?.let {
                _uiState.update { state ->
                    state.copy(
                        title = it.title,
                        description = it.description,
                        dueDate = it.dueDate,
                        dueTime = it.dueTime,
                        priority = it.priority,
                        status = it.status,
                        isLoading = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun updateDueDate(date: LocalDate) {
        _uiState.update { it.copy(dueDate = date) }
    }
    
    fun updateDueTime(time: LocalTime) {
        _uiState.update { it.copy(dueTime = time) }
    }
    
    fun updatePriority(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }
    
    fun updateStatus(status: Status) {
        _uiState.update { it.copy(status = status) }
    }
    
    fun saveAssignment() {
        val state = _uiState.value
        
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val assignment = Assignment(
                id = assignmentId ?: 0,
                title = state.title,
                description = state.description,
                dueDate = state.dueDate,
                dueTime = state.dueTime,
                priority = state.priority,
                status = state.status,
                createdAt = now,
                updatedAt = now
            )
            
            if (assignmentId == null) {
                createAssignmentUseCase(assignment)
            } else {
                updateAssignmentUseCase(assignment)
            }
            
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
