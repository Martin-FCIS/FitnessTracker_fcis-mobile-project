package com.example.fitness_tracker.data.history

import androidx.compose.ui.graphics.vector.ImageVector

data class HistoryLog(
    val time: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val type: LogType
)
enum class LogType { WATER, WALK, FOOD }
