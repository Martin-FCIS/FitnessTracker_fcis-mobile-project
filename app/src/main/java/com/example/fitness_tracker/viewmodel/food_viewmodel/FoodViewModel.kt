package com.example.fitness_tracker.viewmodel.food_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.data.food.FoodEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FoodViewModel(
    private val foodRepo: FoodRepo,
    private val authRepo: AuthRepo,
    private val userPrefs: UserPreferences
) : ViewModel() {

    val dailyCalorieGoal: StateFlow<Int> = userPrefs.dailyCalorieGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

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

    val todayFoods: StateFlow<List<FoodEntity>> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                foodRepo.getTodayFoods(uid, startOfDay)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCalories: StateFlow<Int> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                foodRepo.getTodayCalories(uid, startOfDay).map { it ?: 0 }
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayProtein: StateFlow<Double> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                foodRepo.getTodayProtein(uid, startOfDay).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayCarbs: StateFlow<Double> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                foodRepo.getTodayCarbs(uid, startOfDay).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayFats: StateFlow<Double> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                foodRepo.getTodayFats(uid, startOfDay).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addFood(name: String, weight: Double, protein: Double, carbs: Double, fats: Double) {
        val currentUserId = getCurrentUserId()
        if (currentUserId.isNotEmpty()) {
            val calculatedCalories = ((protein * 4) + (carbs * 4) + (fats * 9)).toInt()

            val food = FoodEntity(
                userId = currentUserId,
                foodName = name,
                weight = weight,
                protein = protein,
                carbs = carbs,
                fats = fats,
                totalCalories = calculatedCalories,
                timestamp = System.currentTimeMillis()
            )

            viewModelScope.launch {
                foodRepo.insertFood(food)
            }
        }
    }

    fun deleteFood(food: FoodEntity) {
        viewModelScope.launch {
            foodRepo.deleteFood(food)
        }
    }
}