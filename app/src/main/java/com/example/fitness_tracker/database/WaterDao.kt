package com.example.fitness_tracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Insert
    suspend fun insertRecord(record: WaterEntity)

    @Delete
    suspend fun deleteRecord(record: WaterEntity)

    @Query("SELECT * FROM water_table ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<WaterEntity>>

    @Query("SELECT SUM(amount) FROM water_table WHERE timestamp >= :startOfDay")
    fun getTodayTotal(startOfDay: Long):Flow<Int?>
}