//Custom UI widgets or reusable UI components
package com.example.fitlife.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.fitlife.MainActivity
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import java.text.SimpleDateFormat
import java.util.*

class ProteinWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is removed
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = Prefs(context)
            val views = RemoteViews(context.packageName, R.layout.widget_protein)

            // Calculate protein progress
            val proteinProgress = calculateProteinProgress(prefs)
            views.setTextViewText(R.id.text_protein_progress, "${proteinProgress.consumed}g / ${proteinProgress.target}g")
            views.setProgressBar(R.id.progress_protein, 100, proteinProgress.percentage, false)

            // Calculate habits progress
            val habitsProgress = calculateHabitsProgress(prefs)
            views.setTextViewText(R.id.text_habits_progress, "${habitsProgress.completed} / ${habitsProgress.total}")
            views.setProgressBar(R.id.progress_habits, 100, habitsProgress.percentage, false)

            // Set up click intent to open MainActivity
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.text_protein_title, pendingIntent)
            views.setOnClickPendingIntent(R.id.text_habits_title, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun calculateProteinProgress(prefs: Prefs): ProteinProgress {
            val foodLogs = prefs.getFoodLogs()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val todayLogs = foodLogs.filter { log ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(log.date) == today
            }
            
            val consumedProtein = todayLogs.sumOf { it.totalProtein.toDouble() }.toFloat()
            
            // Get profile for target calculation
            val profile = prefs.getProfile()
            val targetProtein = calculateProteinTarget(profile?.weight ?: 70f, profile?.activityLevel)
            
            val percentage = if (targetProtein > 0) {
                ((consumedProtein / targetProtein) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            
            return ProteinProgress(
                consumed = consumedProtein.toInt(),
                target = targetProtein.toInt(),
                percentage = percentage
            )
        }

        private fun calculateHabitsProgress(prefs: Prefs): HabitsProgress {
            val habits = prefs.getHabits()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            val activeHabits = habits.filter { it.isActive }
            val completedToday = activeHabits.count { habit ->
                habit.completedDates.contains(today)
            }
            
            val percentage = if (activeHabits.isNotEmpty()) {
                (completedToday * 100) / activeHabits.size
            } else {
                0
            }
            
            return HabitsProgress(
                completed = completedToday,
                total = activeHabits.size,
                percentage = percentage
            )
        }

        private fun calculateProteinTarget(weightKg: Float, activityLevel: com.example.fitlife.model.ActivityLevel?): Float {
            // Basic protein calculation: 1.2-2.0g per kg body weight
            // Using 1.6g as a reasonable target
            val baseProtein = weightKg * 1.6f
            
            // Adjust based on activity level
            return when (activityLevel) {
                com.example.fitlife.model.ActivityLevel.SEDENTARY -> baseProtein * 0.9f
                com.example.fitlife.model.ActivityLevel.LIGHTLY_ACTIVE -> baseProtein
                com.example.fitlife.model.ActivityLevel.MODERATE -> baseProtein * 1.1f
                com.example.fitlife.model.ActivityLevel.VERY_ACTIVE -> baseProtein * 1.2f
                com.example.fitlife.model.ActivityLevel.EXTRA_ACTIVE -> baseProtein * 1.3f
                else -> baseProtein
            }
        }
    }

    private data class ProteinProgress(
        val consumed: Int,
        val target: Int,
        val percentage: Int
    )

    private data class HabitsProgress(
        val completed: Int,
        val total: Int,
        val percentage: Int
    )
}





