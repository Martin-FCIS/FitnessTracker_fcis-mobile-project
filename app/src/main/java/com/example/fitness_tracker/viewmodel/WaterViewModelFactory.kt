package com.example.fitness_tracker.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.database.WaterRepo
import com.example.fitness_tracker.database.UserPreferences

class WaterViewModelFactory(private val repository: WaterRepo, private val userPrefs: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repository,userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}