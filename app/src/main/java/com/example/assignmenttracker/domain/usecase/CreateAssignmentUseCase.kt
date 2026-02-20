package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import com.example.assignmenttracker.util.NotificationScheduler
import javax.inject.Inject

class CreateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(assignment: Assignment): Long {
        val id = repository.insertAssignment(assignment)
        notificationScheduler.scheduleReminder(assignment.copy(id = id))
        return id
    }
}
