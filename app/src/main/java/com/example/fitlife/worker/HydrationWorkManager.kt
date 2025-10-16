package com.example.fitlife.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class HydrationWorkManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleHydrationReminders(intervalMinutes: Int) {
        // Cancel any existing hydration reminders first
        cancelHydrationReminders()

        // Create constraints for the work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        // Create the periodic work request
        val hydrationWorkRequest = PeriodicWorkRequestBuilder<HydrationWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(HydrationWorker.WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        // Enqueue the work
        workManager.enqueueUniquePeriodicWork(
            HydrationWorker.WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationWorkRequest
        )
    }

    fun cancelHydrationReminders() {
        workManager.cancelAllWorkByTag(HydrationWorker.WORK_TAG)
    }

    fun isHydrationWorkScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosByTag(HydrationWorker.WORK_TAG).get()
        return workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }

    fun getHydrationWorkStatus(): List<WorkInfo> {
        return workManager.getWorkInfosByTag(HydrationWorker.WORK_TAG).get()
    }
}
