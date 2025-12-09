package com.example.fitness_tracker.Repository

import com.example.fitness_tracker.data.walk.WalkDao
import com.example.fitness_tracker.data.walk.WalkEntity
import kotlinx.coroutines.flow.Flow

class WalkRepo(private val walkDao: WalkDao) {

    suspend fun insertWalk(walk: WalkEntity) {
        walkDao.insertWalk(walk)
    }
    fun getTodaySteps(uid: String, date: String): Flow<Int?> {
        return walkDao.getTodayTotalSteps(uid, date)
    }

    fun getTodayCalories(uid: String, date: String): Flow<Int?> {
        return walkDao.getTodayCaloriesBurned(uid, date)
    }

    fun getTodayDistance(uid: String, date: String): Flow<Double?> {
        return walkDao.getTodayDistance(uid, date)
    }
    fun getWalkForDay(uid: String, date: String): Flow<WalkEntity?> {
        return walkDao.getWalkForDay(uid, date)
    }
}