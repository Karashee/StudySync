package com.example.assignmenttracker.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.assignmenttracker.data.local.dao.AssignmentDao
import com.example.assignmenttracker.data.local.database.AppDatabase
import com.example.assignmenttracker.util.NotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "assignment_tracker_db"
        ).build()
    }
    
    @Provides
    fun provideAssignmentDao(database: AppDatabase): AssignmentDao {
        return database.assignmentDao()
    }
    
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context,
        workManager: WorkManager
    ): NotificationScheduler {
        return NotificationScheduler(context, workManager)
    }
}
