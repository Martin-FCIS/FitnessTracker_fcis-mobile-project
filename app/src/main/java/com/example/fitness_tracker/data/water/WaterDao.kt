package com.example.fitness_tracker.data.water

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

    @Query("SELECT * FROM water_table WHERE userId = :uid ORDER BY timestamp DESC")
    fun getRecordsForUser(uid: String): Flow<List<WaterEntity>>

    @Query("SELECT SUM(amount) FROM water_table WHERE userId = :uid AND timestamp >= :startOfDay")
    fun getTodayTotalForUser(uid: String, startOfDay: Long): Flow<Int?>

    @Query("SELECT * FROM water_table WHERE userId = :uid AND timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getWaterForDay(uid: String, start: Long, end: Long): Flow<List<WaterEntity>>
}