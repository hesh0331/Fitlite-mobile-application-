package com.example.fitlife.model

import java.io.Serializable
import java.util.Date

data class Habit(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: HabitCategory = HabitCategory.GENERAL,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetValue: Int = 1,
    val unit: String = "",
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val completionHistory: List<HabitCompletion> = emptyList(),
    val completedDates: List<String> = emptyList() // Format: "yyyy-MM-dd"
) : Serializable

data class HabitCompletion(
    val id: String = "",
    val habitId: String = "",
    val date: Date = Date(),
    val completedValue: Int = 1,
    val notes: String = "",
    val isCompleted: Boolean = true
)

enum class HabitCategory {
    HEALTH,
    FITNESS,
    NUTRITION,
    MENTAL_HEALTH,
    PRODUCTIVITY,
    GENERAL
}

enum class HabitFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}
