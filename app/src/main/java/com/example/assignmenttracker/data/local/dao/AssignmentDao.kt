package com.example.assignmenttracker.data.local.dao

import androidx.room.*
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC, dueTime ASC")
    fun getAllAssignments(): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getAssignmentById(id: Long): Assignment?
    
    @Query("SELECT * FROM assignments WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY dueDate ASC, dueTime ASC")
    fun searchAssignments(query: String): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE status = :status ORDER BY dueDate ASC, dueTime ASC")
    fun getAssignmentsByStatus(status: Status): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE priority = :priority ORDER BY dueDate ASC, dueTime ASC")
    fun getAssignmentsByPriority(priority: Priority): Flow<List<Assignment>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: Assignment): Long
    
    @Update
    suspend fun updateAssignment(assignment: Assignment)
    
    @Delete
    suspend fun deleteAssignment(assignment: Assignment)
    
    @Query("SELECT * FROM assignments WHERE dueDate < :today AND status != 'DONE' ORDER BY dueDate ASC, dueTime ASC")
    fun getOverdueAssignments(today: LocalDate): Flow<List<Assignment>>
    
    @Query("SELECT * FROM assignments WHERE status = 'DONE' AND updatedAt >= :startOfWeek ORDER BY updatedAt DESC")
    fun getCompletedThisWeek(startOfWeek: LocalDateTime): Flow<List<Assignment>>
}
