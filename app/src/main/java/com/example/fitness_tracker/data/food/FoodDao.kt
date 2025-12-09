package com.example.fitness_tracker.data.food

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert
    suspend fun insertFood(food: FoodEntity)

    @Delete
    suspend fun deleteFood(food: FoodEntity)

    @Query("SELECT * FROM food_table WHERE userId = :uid AND timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayFoods(uid: String, startOfDay: Long): Flow<List<FoodEntity>>

    @Query("SELECT SUM(totalCalories) FROM food_table WHERE userId = :uid AND timestamp >= :startOfDay")
    fun getTodayTotalCalories(uid: String, startOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(protein) FROM food_table WHERE userId = :uid AND timestamp >= :startOfDay")
    fun getTodayProtein(uid: String, startOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(carbs) FROM food_table WHERE userId = :uid AND timestamp >= :startOfDay")
    fun getTodayCarbs(uid: String, startOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(fats) FROM food_table WHERE userId = :uid AND timestamp >= :startOfDay")
    fun getTodayFats(uid: String, startOfDay: Long): Flow<Double?>
}