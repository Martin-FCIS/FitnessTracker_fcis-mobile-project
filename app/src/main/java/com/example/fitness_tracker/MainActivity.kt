package com.example.fitness_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_tracker.database.AppDatabase
import com.example.fitness_tracker.database.UserPreferences
import com.example.fitness_tracker.database.WaterRepo
import com.example.fitness_tracker.ui.theme.Fitness_TrackerTheme
import com.example.fitness_tracker.viewmodel.WaterViewModel
import com.example.fitness_tracker.viewmodel.WaterViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = WaterRepo(database.waterDao())
        val userPreferences = UserPreferences(applicationContext)
        val viewModelFactory = WaterViewModelFactory(repository, userPreferences)

        setContent {
            Fitness_TrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: WaterViewModel = viewModel(factory = viewModelFactory)

                    val isSetupDone by viewModel.isSetupDone.collectAsState()
                    val dailyGoal by viewModel.dailyGoal.collectAsState()
                    val totalIntake by viewModel.totalIntake.collectAsState()
                    val history by viewModel.records.collectAsState()

                    if (isSetupDone) {
                        TrackerScreen(
                            currentIntake = totalIntake,
                            dailyGoal = dailyGoal,
                            history = history,
                            onAddWater = { amount -> viewModel.addWater(amount) },
                            onDelete = { record -> viewModel.deleteWater(record) },
                            onUpdateWeight = { newWeight -> viewModel.saveUserWeight(newWeight) }
                        )
                    } else {
                        SetupScreen(
                            onSave = { weight -> viewModel.saveUserWeight(weight) }
                        )
                    }
                }
            }
        }
    }
}