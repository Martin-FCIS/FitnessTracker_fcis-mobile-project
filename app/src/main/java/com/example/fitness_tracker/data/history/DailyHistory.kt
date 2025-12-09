package com.example.fitness_tracker.data.history

data class DailyHistory(
    val date: String,
    val totalWater: Int,
    val totalCalories: Int,
    val totalSteps: Int
)
