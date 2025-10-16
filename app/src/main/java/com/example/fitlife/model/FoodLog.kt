//Defines data structures
package com.example.fitlife.model

import java.util.Date

data class FoodLog(
    val id: String = "",
    val date: Date = Date(),
    val mealType: MealType = MealType.SNACK,
    val foodItems: List<FoodItem> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f, // in grams
    val totalCarbs: Float = 0f, // in grams
    val totalFat: Float = 0f, // in grams
    val totalFiber: Float = 0f, // in grams
    val totalSugar: Float = 0f, // in grams
    val totalSodium: Float = 0f, // in mg
    val notes: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class FoodItem(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val quantity: Float = 0f,
    val unit: String = "", // g, ml, cup, piece, etc.
    val calories: Int = 0,
    val protein: Float = 0f, // in grams
    val carbs: Float = 0f, // in grams
    val fat: Float = 0f, // in grams
    val fiber: Float = 0f, // in grams
    val sugar: Float = 0f, // in grams
    val sodium: Float = 0f, // in mg
    val vitamins: Map<String, Float> = emptyMap(), // vitamin name to amount
    val minerals: Map<String, Float> = emptyMap(), // mineral name to amount
    val barcode: String = "",
    val imageUrl: String = "",
    val category: FoodCategory = FoodCategory.OTHER
)

enum class MealType(val description: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack"),
    DRINK("Drink")
}

enum class FoodCategory(val description: String) {
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    GRAINS("Grains"),
    PROTEIN("Protein"),
    DAIRY("Dairy"),
    BEVERAGES("Beverages"),
    SNACKS("Snacks"),
    DESSERTS("Desserts"),
    CONDIMENTS("Condiments"),
    SUPPLEMENTS("Supplements"),
    OTHER("Other")
}

data class HydrationLog(
    val id: String = "",
    val date: Date = Date(),
    val waterIntake: Float = 0f, // in ml
    val otherBeverages: List<BeverageEntry> = emptyList(),
    val totalFluidIntake: Float = 0f, // in ml
    val goal: Float = 2000f, // daily goal in ml
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class BeverageEntry(
    val id: String = "",
    val name: String = "",
    val type: BeverageType = BeverageType.WATER,
    val quantity: Float = 0f,
    val unit: String = "ml",
    val calories: Int = 0,
    val caffeine: Float = 0f, // in mg
    val timestamp: Date = Date()
)

enum class BeverageType(val description: String) {
    WATER("Water"),
    COFFEE("Coffee"),
    TEA("Tea"),
    JUICE("Juice"),
    SODA("Soda"),
    ENERGY_DRINK("Energy Drink"),
    ALCOHOL("Alcohol"),
    MILK("Milk"),
    SPORTS_DRINK("Sports Drink"),
    OTHER("Other")
}
