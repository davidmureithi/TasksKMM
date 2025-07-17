package com.example.tasklist.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tasklist.data.repository.TaskRepository
import com.example.tasklist.db.DriverFactory
import com.example.tasklist.db.TaskDatabase
import java.util.concurrent.TimeUnit
import com.example.tasklist.domain.model.Task
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.coroutines.flow.toList

class DueDateNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d("DueDateWorker", "Worker started")
        val tasks = fetchTasks()
        Log.d("DueDateWorker", "Fetched ${tasks.size} incomplete tasks")
        val now = Clock.System.now()
        val tomorrow = now.plus(24 * 60 * 60 * 1000, DateTimeUnit.MILLISECOND)
        val dueSoonTasks = tasks.filter {
            it.dueDate != null &&
            it.dueDate > now &&
            it.dueDate <= tomorrow &&
            !it.isCompleted
        }
        Log.d("DueDateWorker", "Found ${dueSoonTasks.size} tasks due soon")
        dueSoonTasks.forEach { task ->
            sendDueDateNotification(task)
            Log.d("DueDateWorker", "Notification sent for task: ${task.title}")
        }
        return Result.success()
    }

    private fun fetchTasks(): List<Task> {
        val driverFactory = DriverFactory(applicationContext)
        val database = TaskDatabase(driverFactory.createDriver())
        val repository = TaskRepository(database)
        // Fetch incomplete tasks synchronously
        return runBlocking {
            repository.getIncompleteTasks().toList().firstOrNull() ?: emptyList()
        }
    }

    private fun sendDueDateNotification(task: Task) {
        val channelId = "due_date_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Due Date Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Task Due Soon: ${task.title}")
            .setContentText("Due: ${task.dueDate}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        notificationManager.notify(task.id.toInt(), notification)
    }

    companion object {
        fun schedule(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<DueDateNotificationWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        fun immediateSchedule(context: Context) {
            val workRequest = androidx.work.OneTimeWorkRequestBuilder<DueDateNotificationWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
