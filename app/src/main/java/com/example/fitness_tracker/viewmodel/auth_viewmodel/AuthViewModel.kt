package com.example.fitness_tracker.viewmodel.auth_viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.database.User
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel(private val authRepo: AuthRepo, private val userPrefs: UserPreferences) :
    ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile


    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepo.login(email, pass)
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Unknown wrong happened")
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        pass: String,
        age: String,
        gender: String,
        height: String,
        weight: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val ageInt = age.toIntOrNull() ?: 0
            val heightDouble = height.toDoubleOrNull() ?: 0.0
            val weightDouble = weight.toDoubleOrNull() ?: 0.0

            if (ageInt == 0 || heightDouble == 0.0 || weightDouble == 0.0) {
                _authState.value = AuthState.Error("please enter a valid numbers")
                return@launch
            }
            if (name.isBlank()) {
                _authState.value = AuthState.Error("Please enter your name")
                return@launch
            }

            val result =
                authRepo.signUp(name, email, pass, ageInt, gender, heightDouble, weightDouble)

            result.onSuccess {
                userPrefs.saveGoal(weightDouble.toInt())

                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "failed")
            }
        }
    }

    fun fetchUser() {
        val uid = authRepo.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                val result = authRepo.getUser(uid)
                result.onSuccess { user ->
                    _currentUserProfile.value = user
                }
            }
        }
    }

    fun updateUser(name: String, age: String, height: String, weight: String, gender: String) {
        val uid = authRepo.getCurrentUserId() ?: return
        val currentEmail = _currentUserProfile.value?.email ?: ""
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val ageInt = age.toIntOrNull() ?: 0
            val heightDouble = height.toDoubleOrNull() ?: 0.0
            val weightDouble = weight.toDoubleOrNull() ?: 0.0

            if (name.isBlank() || ageInt == 0 || heightDouble == 0.0 || weightDouble == 0.0) {
                _authState.value = AuthState.Error("Invalid Data")
                return@launch
            }
            val updatedUser = User(
                uid = uid,
                name = name,
                email = currentEmail,
                age = ageInt,
                gender = gender,
                height = heightDouble,
                weight = weightDouble
            )
            val result = authRepo.updateUser(updatedUser)
            result.onSuccess {
                userPrefs.saveGoal(weightDouble.toInt())

                _currentUserProfile.value = updatedUser
                _authState.value = AuthState.Success
            }.onFailure {
                _authState.value = AuthState.Error(it.message ?: "Update Failed")
            }
        }
    }

    fun logout() {
        authRepo.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}