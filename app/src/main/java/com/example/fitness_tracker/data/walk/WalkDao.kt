package com.example.fitness_tracker.data.walk

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkDao {
    @Insert
    suspend fun insertWalk(walk: WalkEntity)

    @Query("SELECT SUM(steps) FROM walk_table WHERE userId = :uid AND date = :todayDate")
    fun getTodayTotalSteps(uid: String, todayDate: String): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM walk_table WHERE userId = :uid AND date = :todayDate")
    fun getTodayCaloriesBurned(uid: String, todayDate: String): Flow<Int?>

    @Query("SELECT SUM(distance) FROM walk_table WHERE userId = :uid AND date = :todayDate")
    fun getTodayDistance(uid: String, todayDate: String): Flow<Double?>
}