package com.example.fitness_tracker.viewmodel.food_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.data.UserPreferences

class FoodViewModelFactory(
    private val repo: FoodRepo,
    private val authRepo: AuthRepo,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repo,authRepo ,userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
