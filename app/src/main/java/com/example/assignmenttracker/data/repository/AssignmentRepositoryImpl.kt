package com.example.assignmenttracker.data.repository

import com.example.assignmenttracker.data.local.dao.AssignmentDao
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val dao: AssignmentDao
) : AssignmentRepository {
    
    override fun getAllAssignments(): Flow<List<Assignment>> = dao.getAllAssignments()
    
    override suspend fun getAssignmentById(id: Long): Assignment? = dao.getAssignmentById(id)
    
    override fun searchAssignments(query: String): Flow<List<Assignment>> = 
        dao.searchAssignments(query)
    
    override fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>> = 
        dao.getAssignmentsByStatus(status)
    
    override fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>> = 
        dao.getAssignmentsByPriority(priority)
    
    override suspend fun insertAssignment(assignment: Assignment): Long = 
        dao.insertAssignment(assignment)
    
    override suspend fun updateAssignment(assignment: Assignment) = 
        dao.updateAssignment(assignment)
    
    override suspend fun deleteAssignment(assignment: Assignment) = 
        dao.deleteAssignment(assignment)
    
    override fun getOverdueAssignments(): Flow<List<Assignment>> = 
        dao.getOverdueAssignments(LocalDate.now())
    
    override fun getCompletedThisWeek(): Flow<List<Assignment>> {
        val startOfWeek = LocalDateTime.now()
            .with(DayOfWeek.MONDAY)
            .toLocalDate()
            .atStartOfDay()
        return dao.getCompletedThisWeek(startOfWeek)
    }
}
