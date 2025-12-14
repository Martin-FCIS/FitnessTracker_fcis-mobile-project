package com.example.fitness_tracker.viewmodels.history_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.Repository.WaterRepo

class HistoryViewModelFactory(
    private val waterRepo: WaterRepo,
    private val foodRepo: FoodRepo,
    private val walkRepo: WalkRepo,
    private val authRepo: AuthRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryViewModel(waterRepo, foodRepo, walkRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
