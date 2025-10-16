package com.example.fitlife.model

import java.util.Date

data class Profile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val dateOfBirth: Date? = null,
    val gender: Gender = Gender.OTHER,
    val height: Float = 0f, // in cm
    val weight: Float = 0f, // in kg
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessGoals: List<FitnessGoal> = emptyList(),
    val healthConditions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val dietaryPreferences: List<DietaryPreference> = emptyList(),
    val notificationsEnabled: Boolean = true,
    val reminderTimes: List<String> = emptyList(), // HH:mm format
    val units: MeasurementUnits = MeasurementUnits.METRIC,
    val theme: AppTheme = AppTheme.SYSTEM,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}

enum class ActivityLevel(val description: String, val multiplier: Float) {
    SEDENTARY("Little to no exercise", 1.2f),
    LIGHTLY_ACTIVE("Light exercise 1-3 days/week", 1.375f),
    MODERATE("Moderate exercise 3-5 days/week", 1.55f),
    VERY_ACTIVE("Heavy exercise 6-7 days/week", 1.725f),
    EXTRA_ACTIVE("Very heavy exercise, physical job", 1.9f)
}

enum class FitnessGoal(val description: String) {
    WEIGHT_LOSS("Lose Weight"),
    WEIGHT_GAIN("Gain Weight"),
    MAINTAIN_WEIGHT("Maintain Weight"),
    BUILD_MUSCLE("Build Muscle"),
    IMPROVE_ENDURANCE("Improve Endurance"),
    GENERAL_FITNESS("General Fitness")
}

enum class DietaryPreference(val description: String) {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    PESCATARIAN("Pescatarian"),
    KETO("Ketogenic"),
    PALEO("Paleo"),
    MEDITERRANEAN("Mediterranean"),
    LOW_CARB("Low Carb"),
    GLUTEN_FREE("Gluten Free"),
    DAIRY_FREE("Dairy Free"),
    NONE("No Restrictions")
}

enum class MeasurementUnits {
    METRIC, // kg, cm, ml
    IMPERIAL // lbs, ft/in, fl oz
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
