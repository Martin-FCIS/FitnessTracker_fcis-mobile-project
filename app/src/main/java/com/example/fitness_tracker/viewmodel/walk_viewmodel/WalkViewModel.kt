package com.example.fitness_tracker.viewmodel.walk_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.data.walk.WalkEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WalkViewModel(
    private val walkRepo: WalkRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    private fun getCurrentUserId(): String = authRepo.getCurrentUserId() ?: ""

    private val todayDate: String
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return format.format(Date())
        }

    val todaySteps: StateFlow<Int> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                walkRepo.getTodaySteps(uid, todayDate).map { it ?: 0 }
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayDistance: StateFlow<Double> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                walkRepo.getTodayDistance(uid, todayDate).map { it ?: 0.0 }
            } else {
                flowOf(0.0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayCalories: StateFlow<Int> = flowOf(Unit)
        .flatMapLatest {
            val uid = getCurrentUserId()
            if (uid.isNotEmpty()) {
                walkRepo.getTodayCalories(uid, todayDate).map { it ?: 0 }
            } else {
                flowOf(0)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addSteps(steps: Int) {
        val currentUserId = getCurrentUserId()
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            val distanceKm = steps * 0.0008
            val calories = (steps * 0.04).toInt()
            val walk = WalkEntity(
                userId = currentUserId,
                date = todayDate,
                steps = steps,
                distance = distanceKm,
                caloriesBurned = calories
            )
            walkRepo.insertWalk(walk)
        }
    }
}