package com.example.fitness_tracker.viewmodel.auth_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.database.UserPreferences

class AuthViewModelFactory(private val repository: AuthRepo, private val userPrefs: UserPreferences): ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}