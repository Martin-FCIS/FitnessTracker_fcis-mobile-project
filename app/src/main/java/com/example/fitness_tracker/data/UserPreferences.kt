package com.example.fitness_tracker.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



private val Context.dataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val DAILY_GOAL_KEY = intPreferencesKey("daily_goal")
        val IS_SETUP_DONE_KEY = booleanPreferencesKey("is_setup_done")
    }
    val dailyGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_GOAL_KEY] ?: 2000
        }
    val isSetupDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_SETUP_DONE_KEY] == true
    }
    suspend fun saveGoal(weight: Int) {
        val calculatedGoal = weight * 35
        context.dataStore.edit { prefs ->
            prefs[DAILY_GOAL_KEY] = calculatedGoal
            prefs[IS_SETUP_DONE_KEY] = true
        }
    }
}