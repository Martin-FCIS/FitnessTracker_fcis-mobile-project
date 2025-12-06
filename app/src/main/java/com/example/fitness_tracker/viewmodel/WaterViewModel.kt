package com.example.fitness_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.database.WaterEntity
import com.example.fitness_tracker.database.WaterRepo
import com.example.fitness_tracker.database.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(private val repository: WaterRepo, private val userPrefs: UserPreferences) :
    ViewModel() {
    val dailyGoal: StateFlow<Int> = userPrefs.dailyGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    val isSetupDone: StateFlow<Boolean> = userPrefs.isSetupDone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun saveUserWeight(weight: Int) {
        viewModelScope.launch {
            userPrefs.saveGoal(weight)
        }
    }

    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    val records: StateFlow<List<WaterEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIntake: StateFlow<Int> = repository.getTodayTotal(startOfDay)
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    fun addWater(amount: Int) {
        viewModelScope.launch {
            repository.insert(WaterEntity(amount = amount, timestamp = System.currentTimeMillis()))
        }
    }


    fun deleteWater(record: WaterEntity) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }
}