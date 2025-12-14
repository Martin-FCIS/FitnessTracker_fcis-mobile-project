package com.example.fitness_tracker.data.walk

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walk_table")
data class WalkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val date: String,
    val steps: Int,
    val distance: Double,
    val caloriesBurned: Int,

    val timestamp: Long = System.currentTimeMillis()
)