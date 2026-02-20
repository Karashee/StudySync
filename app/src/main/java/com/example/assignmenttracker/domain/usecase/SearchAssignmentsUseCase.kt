package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchAssignmentsUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(
        query: String,
        statusFilter: Status? = null,
        priorityFilter: Priority? = null
    ): Flow<List<Assignment>> {
        return if (query.isBlank() && statusFilter == null && priorityFilter == null) {
            repository.getAllAssignments()
        } else {
            repository.searchAssignments(query).map { assignments ->
                assignments.filter { assignment ->
                    (statusFilter == null || assignment.status == statusFilter) &&
                    (priorityFilter == null || assignment.priority == priorityFilter)
                }
            }
        }
    }
}
