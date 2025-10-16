//include shared preferences, databases, or repositories to store user data, settings,

package com.example.fitlife.data

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import com.example.fitlife.R
import com.example.fitlife.model.*
import com.example.fitlife.widget.ProteinWidgetProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Prefs(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "fitlife_prefs"

        // Keys for different data types
        const val KEY_HABITS = "habits"
        const val KEY_MOODS = "moods"
        const val KEY_PROFILE = "profile"
        const val KEY_FOOD_LOGS = "food_logs"
        const val KEY_HYDRATION_INTERVAL_MIN = "hydration_interval_min"
        const val KEY_HYDRATION_ENABLED = "hydration_enabled"

        // User session keys
        const val KEY_USER_EMAIL = "user_email" // kept for compatibility
        const val KEY_USERNAME = "username"
        const val KEY_USER_PASSWORD = "user_password"
        const val KEY_USER_LOGGED_IN = "user_logged_in"

        // Additional keys for other data
        const val KEY_HYDRATION_LOGS = "hydration_logs"
        const val KEY_APP_SETTINGS = "app_settings"
        const val KEY_LAST_SYNC = "last_sync"
        const val KEY_FIRST_LAUNCH = "first_launch"
    }

    // Habits
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null)

        // If no habits exist and it's first launch, create default habits
        if (json == null && isFirstLaunch()) {
            val defaultHabits = createDefaultHabits()
            saveHabits(defaultHabits)
            setFirstLaunch(false) // Mark that we've initialized
            return defaultHabits
        }

        return if (json != null) {
            try {
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        val existingIndex = habits.indexOfFirst { it.id == habit.id }
        if (existingIndex >= 0) {
            habits[existingIndex] = habit
        } else {
            habits.add(habit)
        }
        saveHabits(habits)
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)
    }

    // Mood Entries
    fun saveMoodEntries(moods: List<MoodEntry>) {
        val json = gson.toJson(moods)
        prefs.edit().putString(KEY_MOODS, json).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOODS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMoodEntry(moodEntry: MoodEntry) {
        val moods = getMoodEntries().toMutableList()
        val existingIndex = moods.indexOfFirst { it.id == moodEntry.id }
        if (existingIndex >= 0) {
            moods[existingIndex] = moodEntry
        } else {
            moods.add(moodEntry)
        }
        saveMoodEntries(moods)
    }

    fun getMoodEntryByDate(date: Date): MoodEntry? {
        return getMoodEntries().find {
            // Simple date comparison (you might want to improve this)
            it.date.time / (24 * 60 * 60 * 1000) == date.time / (24 * 60 * 60 * 1000)
        }
    }

    // Profile
    fun saveProfile(profile: Profile) {
        val json = gson.toJson(profile)
        prefs.edit().putString(KEY_PROFILE, json).apply()
    }

    fun getProfile(): Profile? {
        val json = prefs.getString(KEY_PROFILE, null) ?: return null
        return try {
            gson.fromJson(json, Profile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Food Logs
    fun saveFoodLogs(foodLogs: List<FoodLog>) {
        val json = gson.toJson(foodLogs)
        prefs.edit().putString(KEY_FOOD_LOGS, json).apply()
    }

    fun getFoodLogs(): List<FoodLog> {
        val json = prefs.getString(KEY_FOOD_LOGS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<FoodLog>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveFoodLog(foodLog: FoodLog) {
        val foodLogs = getFoodLogs().toMutableList()
        val existingIndex = foodLogs.indexOfFirst { it.id == foodLog.id }
        if (existingIndex >= 0) {
            foodLogs[existingIndex] = foodLog
        } else {
            foodLogs.add(foodLog)
        }
        saveFoodLogs(foodLogs)
    }

    fun getFoodLogsByDate(date: Date): List<FoodLog> {
        return getFoodLogs().filter {
            it.date.time / (24 * 60 * 60 * 1000) == date.time / (24 * 60 * 60 * 1000)
        }
    }

    // Hydration Logs
    fun saveHydrationLogs(hydrationLogs: List<HydrationLog>) {
        val json = gson.toJson(hydrationLogs)
        prefs.edit().putString(KEY_HYDRATION_LOGS, json).apply()
    }

    fun getHydrationLogs(): List<HydrationLog> {
        val json = prefs.getString(KEY_HYDRATION_LOGS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<HydrationLog>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveHydrationLog(hydrationLog: HydrationLog) {
        val hydrationLogs = getHydrationLogs().toMutableList()
        val existingIndex = hydrationLogs.indexOfFirst { it.id == hydrationLog.id }
        if (existingIndex >= 0) {
            hydrationLogs[existingIndex] = hydrationLog
        } else {
            hydrationLogs.add(hydrationLog)
        }
        saveHydrationLogs(hydrationLogs)
    }

    // Hydration Settings
    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }

    fun isHydrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_HYDRATION_ENABLED, false) // Default disabled
    }

    fun setHydrationIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL_MIN, minutes).apply()
    }

    fun getHydrationIntervalMinutes(): Int {
        return prefs.getInt(KEY_HYDRATION_INTERVAL_MIN, 30) // Default 30 minutes
    }

    // App Settings
    fun setFirstLaunch(isFirstLaunch: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setLastSyncTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, timestamp).apply()
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(KEY_LAST_SYNC, 0L)
    }

    // Generic methods for any object
    fun <T> saveObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        prefs.edit().putString(key, json).apply()
    }

    fun <T> getObject(key: String, type: Class<T>): T? {
        val json = prefs.getString(key, null) ?: return null
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> getObjectList(key: String, type: TypeToken<List<T>>): List<T> {
        val json = prefs.getString(key, null) ?: return emptyList()
        return try {
            gson.fromJson(json, type.type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Clear all data
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    // Clear specific data type
    fun clearHabits() {
        prefs.edit().remove(KEY_HABITS).apply()
    }

    fun resetToDefaultHabits() {
        clearHabits()
        val defaultHabits = createDefaultHabits()
        saveHabits(defaultHabits)
    }

    fun createDefaultHabits(): List<Habit> {
        val currentDate = Date()
        return listOf(
            Habit(
                id = "habit_1",
                name = "Drink 8 glasses of water",
                description = "Stay hydrated throughout the day",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                targetValue = 8,
                unit = "glasses",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_2",
                name = "Exercise for 30 minutes",
                description = "Get your body moving",
                category = HabitCategory.FITNESS,
                frequency = HabitFrequency.DAILY,
                targetValue = 30,
                unit = "minutes",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_3",
                name = "Read for 20 minutes",
                description = "Expand your knowledge",
                category = HabitCategory.PRODUCTIVITY,
                frequency = HabitFrequency.DAILY,
                targetValue = 20,
                unit = "minutes",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_4",
                name = "Meditate for 10 minutes",
                description = "Practice mindfulness and relaxation",
                category = HabitCategory.MENTAL_HEALTH,
                frequency = HabitFrequency.DAILY,
                targetValue = 10,
                unit = "minutes",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_5",
                name = "Eat 5 servings of fruits/vegetables",
                description = "Get your daily vitamins and minerals",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                targetValue = 5,
                unit = "servings",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_6",
                name = "Get 8 hours of sleep",
                description = "Rest and recover properly",
                category = HabitCategory.HEALTH,
                frequency = HabitFrequency.DAILY,
                targetValue = 8,
                unit = "hours",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_7",
                name = "Take 10,000 steps",
                description = "Stay active throughout the day",
                category = HabitCategory.FITNESS,
                frequency = HabitFrequency.DAILY,
                targetValue = 10000,
                unit = "steps",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_8",
                name = "Practice gratitude",
                description = "Write down 3 things you're grateful for",
                category = HabitCategory.MENTAL_HEALTH,
                frequency = HabitFrequency.DAILY,
                targetValue = 3,
                unit = "items",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_9",
                name = "Learn something new",
                description = "Spend time learning a new skill or topic",
                category = HabitCategory.PRODUCTIVITY,
                frequency = HabitFrequency.DAILY,
                targetValue = 15,
                unit = "minutes",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            ),
            Habit(
                id = "habit_10",
                name = "Connect with loved ones",
                description = "Call or message family/friends",
                category = HabitCategory.GENERAL,
                frequency = HabitFrequency.DAILY,
                targetValue = 1,
                unit = "conversation",
                completedDates = emptyList(),
                completionHistory = emptyList(),
                streak = 0,
                bestStreak = 0,
                createdAt = currentDate,
                updatedAt = currentDate
            )
        )
    }

    fun clearMoods() {
        prefs.edit().remove(KEY_MOODS).apply()
    }

    fun clearProfile() {
        prefs.edit().remove(KEY_PROFILE).apply()
    }

    fun clearFoodLogs() {
        prefs.edit().remove(KEY_FOOD_LOGS).apply()
    }

    fun clearHydrationLogs() {
        prefs.edit().remove(KEY_HYDRATION_LOGS).apply()
    }

    // Widget update methods
    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, ProteinWidgetProvider::class.java)
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.text_protein_progress)
    }

    // Widget update methods
    fun updateWidget() {
        updateWidget(context)
    }

    // User session management
    fun saveUserCredentials(email: String, password: String) {
        prefs.edit()
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_PASSWORD, password)
            .apply()
    }

    fun saveUsernameCredentials(username: String, password: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_PASSWORD, password)
            .apply()
    }

    fun hasCredentials(): Boolean {
        val savedEmail = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val savedUsername = prefs.getString(KEY_USERNAME, "") ?: ""
        val savedPassword = prefs.getString(KEY_USER_PASSWORD, "") ?: ""
        return (savedEmail.isNotEmpty() || savedUsername.isNotEmpty()) && savedPassword.isNotEmpty()
    }

    fun validateUserCredentials(identifier: String, password: String): Boolean {
        val savedEmail = prefs.getString(KEY_USER_EMAIL, "")
        val savedUsername = prefs.getString(KEY_USERNAME, "")
        val savedPassword = prefs.getString(KEY_USER_PASSWORD, "")
        val idMatches = identifier == savedEmail || identifier == savedUsername
        return idMatches && password == savedPassword
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_USER_LOGGED_IN, false)
    }

    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserName(): String? {
        // Extract name from email (part before @) or return default
        val email = getUserEmail()
        return if (email.isNotEmpty() && email.contains("@")) {
            email.substringBefore("@").replaceFirstChar { it.uppercase() }
        } else {
            "Heshani" // Default name
        }
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_USER_LOGGED_IN, false)
            .apply()
    }
}
