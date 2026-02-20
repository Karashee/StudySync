package com.example.assignmenttracker.di

import com.example.assignmenttracker.data.local.dao.AssignmentDao
import com.example.assignmenttracker.data.repository.AssignmentRepositoryImpl
import com.example.assignmenttracker.domain.repository.AssignmentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAssignmentRepository(dao: AssignmentDao): AssignmentRepository {
        return AssignmentRepositoryImpl(dao)
    }
}
