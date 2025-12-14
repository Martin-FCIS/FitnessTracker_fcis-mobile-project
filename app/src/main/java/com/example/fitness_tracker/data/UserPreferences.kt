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
        val DAILY_WATER_GOAL_KEY = intPreferencesKey("daily_water_goal")
        val DAILY_CALORIE_GOAL_KEY = intPreferencesKey("daily_calorie_goal")
        val IS_SETUP_DONE_KEY = booleanPreferencesKey("is_setup_done")
    }
    val dailyWaterGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_WATER_GOAL_KEY] ?: 2000
        }
    val dailyCalorieGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_CALORIE_GOAL_KEY] ?: 2000
    }

    val isSetupDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_SETUP_DONE_KEY] == true
    }
    suspend fun saveUserGoals(weight: Double,height:Double,age:Int,gender:String) {
        val waterGoal = (weight * 35).toInt()
        var bmr = (10 * weight) + (6.25 * height) - (5 * age)
        if (gender.equals("Male", ignoreCase = true)) {
            bmr += 5
        }else{
            bmr -= 161
        }
        val calorieGoal = (bmr * 1.2).toInt()
        context.dataStore.edit { prefs ->
            prefs[DAILY_WATER_GOAL_KEY] = waterGoal
            prefs[DAILY_CALORIE_GOAL_KEY] = calorieGoal
            prefs[IS_SETUP_DONE_KEY] = true
        }
    }
}