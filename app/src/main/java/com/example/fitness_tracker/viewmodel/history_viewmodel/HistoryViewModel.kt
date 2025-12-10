package com.example.fitness_tracker.viewmodel.history_viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.data.history.DailyHistory
import com.example.fitness_tracker.data.history.HistoryLog
import com.example.fitness_tracker.data.history.LogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryViewModel(
    private val waterRepo: WaterRepo,
    private val foodRepo: FoodRepo,
    private val walkRepo: WalkRepo,
    private val authRepo: AuthRepo
) : ViewModel() {
    private val currentUserId: String
        get() = authRepo.getCurrentUserId() ?: ""
    val historyList: StateFlow<List<DailyHistory>> = combine(
        waterRepo.getRecordsForUser(currentUserId),
        foodRepo.getAllFoods(currentUserId),
        walkRepo.getAllWalks(currentUserId)
    ) { waterList, foodList, walkList ->
        val waterMap = waterList.groupBy { formatDate(it.timestamp) }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val foodMap = foodList.groupBy { formatDate(it.timestamp) }
            .mapValues { entry -> entry.value.sumOf { it.totalCalories } }

        val walkMap = walkList.associateBy { it.date }

        val allDates = (waterMap.keys + foodMap.keys + walkMap.keys).toSortedSet(reverseOrder())

        allDates.map { date ->
            DailyHistory(
                date = date,
                totalWater = waterMap[date] ?: 0,
                totalCaloriesConsumed = foodMap[date] ?: 0,
                totalSteps = walkMap[date]?.steps ?: 0,
                totalCaloriesBurned = walkMap[date]?.caloriesBurned ?: 0
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _selectedDayLogs = MutableStateFlow<List<HistoryLog>>(emptyList())
    val selectedDayLogs: StateFlow<List<HistoryLog>> = _selectedDayLogs
    private val _selectedDaySummary = MutableStateFlow<DailyHistory?>(null)
    val selectedDaySummary: StateFlow<DailyHistory?> = _selectedDaySummary
    fun loadDayDetails(dateString: String) {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateString) ?: Date()

            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis - 1

            combine(
                waterRepo.getWaterForDay(currentUserId, startOfDay, endOfDay),
                foodRepo.getFoodsForDay(currentUserId, startOfDay, endOfDay),
                walkRepo.getWalkForDay(currentUserId, dateString)
            ) { waterList, foodList, walkRecord ->
                _selectedDaySummary.value = DailyHistory(
                    date = dateString,
                    totalWater = waterList.sumOf { it.amount },
                    totalCaloriesConsumed = foodList.sumOf { it.totalCalories },
                    totalSteps = walkRecord?.steps ?: 0,
                    totalCaloriesBurned = walkRecord?.caloriesBurned ?: 0
                )

                val logs = mutableListOf<HistoryLog>()

                waterList.forEach {
                    logs.add(HistoryLog(
                        time = formatTime(it.timestamp),
                        title = "Water",
                        subtitle = "Hydration",
                        value = "${it.amount} ml",
                        icon = Icons.Default.WaterDrop,
                        type = LogType.WATER
                    ))
                }

                foodList.forEach {
                    logs.add(HistoryLog(
                        time = formatTime(it.timestamp),
                        title = it.foodName,
                        subtitle = "${it.protein}p • ${it.carbs}c • ${it.fats}f",
                        value = "${it.totalCalories} kcal",
                        icon = Icons.Default.Restaurant,
                        type = LogType.FOOD
                    ))
                }

                if (walkRecord != null && walkRecord.steps > 0) {
                    logs.add(HistoryLog(
                        time = "00:00",
                        title = "Walking",
                        subtitle = "${walkRecord.distance} KM",
                        value = "${walkRecord.steps} steps",
                        icon = Icons.Default.DirectionsWalk,
                        type = LogType.WALK
                    ))
                }

                logs.sortedByDescending { it.time }

            }.collect {
                _selectedDayLogs.value = it
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
    }
}
