package com.example.fitness_tracker.data.water

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "water_table")
data class WaterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId:String,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()

    )
