package com.example.fitness_tracker.Repository
import com.example.fitness_tracker.data.water.WaterDao
import com.example.fitness_tracker.data.water.WaterEntity
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
    fun getWaterForDay(uid: String, start: Long, end: Long): Flow<List<WaterEntity>> {
        return waterDao.getWaterForDay(uid, start,end)
    }

}