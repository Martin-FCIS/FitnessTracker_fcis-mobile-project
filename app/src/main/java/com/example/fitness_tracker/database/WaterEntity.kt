package com.example.fitness_tracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "water_table")
data class WaterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val userId:String
    )
