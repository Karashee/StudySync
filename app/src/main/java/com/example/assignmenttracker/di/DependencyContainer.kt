package com.example.assignmenttracker.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.assignmenttracker.data.local.database.AppDatabase
import com.example.assignmenttracker.data.repository.AssignmentRepositoryImpl
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import com.example.assignmenttracker.domain.usecase.*
import com.example.assignmenttracker.util.NotificationScheduler

/**
 * Manual DI container as fallback if Hilt configuration fails.
 * Usage: Call DependencyContainer.initialize(context) in Application.onCreate()
 */
object DependencyContainer {
    private lateinit var database: AppDatabase
    private lateinit var workManager: WorkManager
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var assignmentRepository: AssignmentRepository
    
    fun initialize(context: Context) {
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "assignment_tracker_db"
        ).build()
        
        workManager = WorkManager.getInstance(context)
        notificationScheduler = NotificationScheduler(context, workManager)
        assignmentRepository = AssignmentRepositoryImpl(database.assignmentDao())
    }
    
    fun provideCreateAssignmentUseCase() = 
        CreateAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideUpdateAssignmentUseCase() = 
        UpdateAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideDeleteAssignmentUseCase() = 
        DeleteAssignmentUseCase(assignmentRepository, notificationScheduler)
    
    fun provideGetAssignmentUseCase() = 
        GetAssignmentUseCase(assignmentRepository)
    
    fun provideGetAllAssignmentsUseCase() = 
        GetAllAssignmentsUseCase(assignmentRepository)
    
    fun provideSearchAssignmentsUseCase() = 
        SearchAssignmentsUseCase(assignmentRepository)
    
    fun provideGetAnalyticsUseCase() = 
        GetAnalyticsUseCase(assignmentRepository)
}
