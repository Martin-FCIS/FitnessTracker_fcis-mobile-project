package com.example.fitness_tracker.database
import kotlinx.coroutines.flow.Flow

class WaterRepo(private val waterDao: WaterDao) {
    val allRecords: Flow<List<WaterEntity>> = waterDao.getAllRecords()
    fun getTodayTotal(startOfDay: Long): Flow<Int?> {
        return waterDao.getTodayTotal(startOfDay)
    }
    suspend fun insert(record: WaterEntity) {
        waterDao.insertRecord(record)
    }
    suspend fun delete(record: WaterEntity) {
        waterDao.deleteRecord(record)
    }

}