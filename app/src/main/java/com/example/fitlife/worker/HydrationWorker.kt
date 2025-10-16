package com.example.fitlife.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fitlife.MainActivity
import com.example.fitlife.R

class HydrationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "hydration_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_TAG = "hydration_reminder_work"
    }

    override fun doWork(): Result {
        return try {
            createNotificationChannel()
            showHydrationNotification()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to stay hydrated throughout the day"
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showHydrationNotification() {
        // Check if notifications are enabled
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create action buttons for the notification
        val drinkWaterIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("action", "log_water")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val drinkWaterPendingIntent = PendingIntent.getActivity(
            applicationContext,
            1,
            drinkWaterIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("action", "snooze_hydration")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val snoozePendingIntent = PendingIntent.getActivity(
            applicationContext,
            2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm) // Using alarm icon for hydration
            .setContentTitle("💧 Time to Hydrate!")
            .setContentText("Stay healthy and drink some water")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("It's time to drink some water! Staying hydrated is essential for your health and well-being. Tap below to log your water intake."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_alarm,
                "Log Water",
                drinkWaterPendingIntent
            )
            .addAction(
                R.drawable.ic_alarm,
                "Snooze 30min",
                snoozePendingIntent
            )
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(false)
            .setTimeoutAfter(300000) // Auto-dismiss after 5 minutes
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
