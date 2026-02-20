package com.example.assignmenttracker.domain.repository

import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAllAssignments(): Flow<List<Assignment>>
    suspend fun getAssignmentById(id: Long): Assignment?
    fun searchAssignments(query: String): Flow<List<Assignment>>
    fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>>
    fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>>
    suspend fun insertAssignment(assignment: Assignment): Long
    suspend fun updateAssignment(assignment: Assignment)
    suspend fun deleteAssignment(assignment: Assignment)
    fun getOverdueAssignments(): Flow<List<Assignment>>
    fun getCompletedThisWeek(): Flow<List<Assignment>>
}
