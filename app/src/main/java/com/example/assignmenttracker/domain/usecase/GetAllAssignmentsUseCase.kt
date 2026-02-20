package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAssignmentsUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): Flow<List<Assignment>> = repository.getAllAssignments()
}
