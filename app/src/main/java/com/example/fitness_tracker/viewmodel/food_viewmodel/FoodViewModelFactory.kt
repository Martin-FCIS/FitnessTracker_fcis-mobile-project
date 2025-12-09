package com.example.fitness_tracker.viewmodel.food_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.data.UserPreferences

class FoodViewModelFactory(
    private val repo: FoodRepo,
    private val userPrefs: UserPreferences,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repo,userPrefs ,userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
