package com.example.fitness_tracker.Repository

import com.example.fitness_tracker.data.food.FoodDao
import com.example.fitness_tracker.data.food.FoodEntity
import kotlinx.coroutines.flow.Flow

class FoodRepo(private val foodDao: FoodDao) {

    suspend fun insertFood(food: FoodEntity) {
        foodDao.insertFood(food)
    }
    suspend fun deleteFood(food: FoodEntity) {
        foodDao.deleteFood(food)
    }
    fun getTodayFoods(uid: String, startOfDay: Long): Flow<List<FoodEntity>> {
        return foodDao.getTodayFoods(uid, startOfDay)
    }

    fun getTodayCalories(uid: String, startOfDay: Long): Flow<Int?> {
        return foodDao.getTodayTotalCalories(uid, startOfDay)
    }
    fun getTodayProtein(uid: String, startOfDay: Long): Flow<Double?> {
        return foodDao.getTodayProtein(uid, startOfDay)
    }

    fun getTodayCarbs(uid: String, startOfDay: Long): Flow<Double?> {
        return foodDao.getTodayCarbs(uid, startOfDay)
    }

    fun getTodayFats(uid: String, startOfDay: Long): Flow<Double?> {
        return foodDao.getTodayFats(uid, startOfDay)
    }
}