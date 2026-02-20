package com.example.assignmenttracker.data.local.database

import androidx.room.TypeConverter
import com.example.assignmenttracker.domain.model.Priority
import com.example.assignmenttracker.domain.model.Status
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
    
    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }
    
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()
    
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }
    
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name
    
    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
    
    @TypeConverter
    fun fromStatus(value: Status): String = value.name
    
    @TypeConverter
    fun toStatus(value: String): Status = Status.valueOf(value)
}
