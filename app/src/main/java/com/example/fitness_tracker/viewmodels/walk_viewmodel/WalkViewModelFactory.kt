package com.example.fitness_tracker.viewmodels.walk_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.WalkRepo

class WalkViewModelFactory(
    private val walkRepo: WalkRepo,
    private val authRepo: AuthRepo
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalkViewModel::class.java)) {
            return WalkViewModel(walkRepo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
