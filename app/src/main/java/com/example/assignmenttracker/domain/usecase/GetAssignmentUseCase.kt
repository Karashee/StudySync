package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import javax.inject.Inject

class GetAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(id: Long): Assignment? = repository.getAssignmentById(id)
}
