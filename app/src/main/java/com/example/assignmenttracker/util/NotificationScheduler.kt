package com.example.assignmenttracker.util

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.assignmenttracker.data.local.entity.Assignment
import com.example.assignmenttracker.worker.ReminderWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationScheduler @Inject constructor(
    private val context: Context,
    private val workManager: WorkManager
) {
    
    companion object {
        // Set to true for testing with 15-second delay
        private const val TEST_MODE = false
        private const val TEST_DELAY_SECONDS = 15L
    }
    
    fun scheduleReminder(assignment: Assignment) {
        val dueDateTime = LocalDateTime.of(assignment.dueDate, assignment.dueTime)
        val reminderTime = dueDateTime.minusHours(24)
        val now = LocalDateTime.now()
        val delay = Duration.between(now, reminderTime)
        
        // Calculate actual delay
        val delayMillis = if (TEST_MODE) {
            TimeUnit.SECONDS.toMillis(TEST_DELAY_SECONDS)
        } else {
            // Don't schedule past reminders
            if (delay.isNegative) return
            delay.toMillis()
        }
        
        val inputData = Data.Builder()
            .putLong("assignment_id", assignment.id)
            .putString("assignment_title", assignment.title)
            .putString("due_date", dueDateTime.toLocalDate().toString())
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${assignment.id}")
            .build()
        
        workManager.enqueueUniqueWork(
            "reminder_${assignment.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelReminder(assignmentId: Long) {
        workManager.cancelUniqueWork("reminder_$assignmentId")
    }
}
