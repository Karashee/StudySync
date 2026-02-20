package com.example.assignmenttracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dueTime: LocalTime,
    val priority: Priority,
    val status: Status,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
