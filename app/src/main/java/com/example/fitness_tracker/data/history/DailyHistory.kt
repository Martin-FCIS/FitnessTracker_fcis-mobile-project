package com.example.fitness_tracker.data.history

data class DailyHistory(
    val date: String,
    val totalCaloriesConsumed: Int,
    val totalWater: Int,
    val totalCaloriesBurned: Int,
    val totalSteps: Int
)
