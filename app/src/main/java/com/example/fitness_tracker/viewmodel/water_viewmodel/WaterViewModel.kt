package com.example.fitness_tracker.viewmodel.water_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.data.water.WaterEntity
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.data.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(
    private val waterRepo: WaterRepo,
    private val authRepo: AuthRepo,
    private val userPrefs: UserPreferences
) : ViewModel() {

    val dailyWaterGoal: StateFlow<Int> = userPrefs.dailyWaterGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    // FIXED: Get currentUserId dynamically, not cached
    private fun getCurrentUserId(): String = authRepo.getCurrentUserId() ?: ""

    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    // FIXED: Use flatMapLatest to react to user changes
    val records: StateFlow<List<WaterEntity>> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                waterRepo.getRecordsForUser(uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIntake: StateFlow<Int> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                waterRepo.getTodayTotalForUser(uid, startOfDay).map { it ?: 0 }
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addWater(amount: Int) {
        val currentUserId = getCurrentUserId()
        if (currentUserId.isNotEmpty()) {
            viewModelScope.launch {
                val newRecord = WaterEntity(
                    amount = amount,
                    timestamp = System.currentTimeMillis(),
                    userId = currentUserId
                )
                waterRepo.insert(newRecord)
            }
        }
    }

    fun deleteWater(record: WaterEntity) {
        viewModelScope.launch {
            waterRepo.delete(record)
        }
    }
}