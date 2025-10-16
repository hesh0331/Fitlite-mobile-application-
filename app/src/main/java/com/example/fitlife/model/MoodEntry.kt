package com.example.fitlife.model

import java.util.Date

data class MoodEntry(
    val id: String = "",
    val date: Date = Date(),
    val moodLevel: MoodLevel = MoodLevel.NEUTRAL,
    val energyLevel: EnergyLevel = EnergyLevel.MODERATE,
    val stressLevel: StressLevel = StressLevel.MODERATE,
    val sleepQuality: SleepQuality = SleepQuality.GOOD,
    val notes: String = "",
    val tags: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val weather: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class MoodLevel(val value: Int, val emoji: String) {
    VERY_SAD(1, "😢"),
    SAD(2, "😔"),
    NEUTRAL(3, "😐"),
    HAPPY(4, "😊"),
    VERY_HAPPY(5, "😄")
}

enum class EnergyLevel(val value: Int, val description: String) {
    VERY_LOW(1, "Very Low"),
    LOW(2, "Low"),
    MODERATE(3, "Moderate"),
    HIGH(4, "High"),
    VERY_HIGH(5, "Very High")
}

enum class StressLevel(val value: Int, val description: String) {
    VERY_LOW(1, "Very Low"),
    LOW(2, "Low"),
    MODERATE(3, "Moderate"),
    HIGH(4, "High"),
    VERY_HIGH(5, "Very High")
}

enum class SleepQuality(val value: Int, val description: String) {
    VERY_POOR(1, "Very Poor"),
    POOR(2, "Poor"),
    FAIR(3, "Fair"),
    GOOD(4, "Good"),
    EXCELLENT(5, "Excellent")
}
