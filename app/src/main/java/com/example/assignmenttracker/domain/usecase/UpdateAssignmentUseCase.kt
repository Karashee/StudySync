package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import com.example.assignmenttracker.util.NotificationScheduler
import javax.inject.Inject

class UpdateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(assignment: Assignment) {
        repository.updateAssignment(assignment)
        notificationScheduler.cancelReminder(assignment.id)
        notificationScheduler.scheduleReminder(assignment)
    }
}
