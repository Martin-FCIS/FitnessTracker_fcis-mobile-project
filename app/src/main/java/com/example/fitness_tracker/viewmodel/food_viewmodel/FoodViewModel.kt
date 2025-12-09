package com.example.fitness_tracker.viewmodel.food_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.data.food.FoodEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FoodViewModel(
    private val repo: FoodRepo,
    private val userPrefs: UserPreferences,
    private val userId: String
) : ViewModel() {

    private val _todayFoods = MutableStateFlow<List<FoodEntity>>(emptyList())
    val todayFoods: StateFlow<List<FoodEntity>> = _todayFoods

    private val _todayCalories = MutableStateFlow(0)
    val todayCalories: StateFlow<Int> = _todayCalories

    private val _todayProtein = MutableStateFlow(0.0)
    val todayProtein: StateFlow<Double> = _todayProtein

    private val _todayCarbs = MutableStateFlow(0.0)
    val todayCarbs: StateFlow<Double> = _todayCarbs

    private val _todayFats = MutableStateFlow(0.0)
    val todayFats: StateFlow<Double> = _todayFats

    private val dailyCalorieGoal = userPrefs.dailyCalorieGoal
    fun get() = dailyCalorieGoal
    fun loadTodayData(startOfDay: Long) {
        repo.getTodayFoods(userId, startOfDay)
            .onEach { _todayFoods.value = it }
            .launchIn(viewModelScope)

        repo.getTodayCalories(userId, startOfDay)
            .onEach { _todayCalories.value = it ?: 0 }
            .launchIn(viewModelScope)

        repo.getTodayProtein(userId, startOfDay)
            .onEach { _todayProtein.value = it ?: 0.0 }
            .launchIn(viewModelScope)

        repo.getTodayCarbs(userId, startOfDay)
            .onEach { _todayCarbs.value = it ?: 0.0 }
            .launchIn(viewModelScope)

        repo.getTodayFats(userId, startOfDay)
            .onEach { _todayFats.value = it ?: 0.0 }
            .launchIn(viewModelScope)
    }

    fun addFood(food: FoodEntity) {
        viewModelScope.launch {
            repo.insertFood(food)
        }
    }

    fun deleteFood(food: FoodEntity) {
        viewModelScope.launch {
            repo.deleteFood(food)
        }
    }
}
