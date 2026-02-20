package com.example.assignmenttracker.domain.usecase

import com.example.assignmenttracker.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class AnalyticsData(
    val completedThisWeek: Int,
    val overdueCount: Int
)

class GetAnalyticsUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(): Flow<AnalyticsData> {
        return combine(
            repository.getCompletedThisWeek(),
            repository.getOverdueAssignments()
        ) { completed, overdue ->
            AnalyticsData(
                completedThisWeek = completed.size,
                overdueCount = overdue.size
            )
        }
    }
}
