package com.example.fitness_tracker.viewmodels.water_viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.data.UserPreferences

class WaterViewModelFactory(private val waterRepo: WaterRepo, private val authRepo: AuthRepo, private val userPrefs: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(waterRepo,authRepo ,userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}