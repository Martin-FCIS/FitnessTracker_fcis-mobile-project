package com.example.fitness_tracker.Repository
import com.example.fitness_tracker.database.WaterDao
import com.example.fitness_tracker.database.WaterEntity
import kotlinx.coroutines.flow.Flow

class WaterRepo(private val waterDao: WaterDao) {

    suspend fun insert(record: WaterEntity) {
        waterDao.insertRecord(record)
    }
    suspend fun delete(record: WaterEntity) {
        waterDao.deleteRecord(record)
    }
    fun getRecordsForUser(uid: String): Flow<List<WaterEntity>> {
        return waterDao.getRecordsForUser(uid)
    }

    fun getTodayTotalForUser(uid: String, startOfDay: Long): Flow<Int?> {
        return waterDao.getTodayTotalForUser(uid, startOfDay)
    }

}