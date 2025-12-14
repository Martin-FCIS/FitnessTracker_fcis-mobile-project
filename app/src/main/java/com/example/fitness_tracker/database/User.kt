package com.example.fitness_tracker.database

data class User(
    val uid: String = "",
    val name:String="",
    val email: String = "",
    val age: Int = 0,
    val gender: String = "Male",
    val height: Double = 0.0,
    val weight: Double = 0.0
)
