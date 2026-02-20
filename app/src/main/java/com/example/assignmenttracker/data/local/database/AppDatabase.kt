package com.example.assignmenttracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.assignmenttracker.data.local.dao.AssignmentDao
import com.example.assignmenttracker.data.local.entity.Assignment

@Database(
    entities = [Assignment::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao
}
