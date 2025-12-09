package com.example.fitness_tracker.data.food

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "food_table")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val foodName: String,
    val weight: Double,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    val totalCalories: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
}