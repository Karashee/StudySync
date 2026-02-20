package com.example.assignmenttracker.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.assignmenttracker.MainActivity
import com.example.assignmenttracker.R

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val assignmentId = inputData.getLong("assignment_id", -1L)
        val assignmentTitle = inputData.getString("assignment_title") ?: "Assignment"
        val dueDate = inputData.getString("due_date") ?: ""
        
        if (assignmentId == -1L) {
            return Result.failure()
        }
        
        showNotification(assignmentId, assignmentTitle, dueDate)
        
        return Result.success()
    }
    
    private fun showNotification(assignmentId: Long, title: String, dueDate: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create intent to open assignment detail
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("assignment_id", assignmentId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            assignmentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(applicationContext, "assignment_reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Assignment Reminder")
            .setContentText("$title is due on $dueDate")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$title is due on $dueDate"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(assignmentId.toInt(), notification)
    }
}
