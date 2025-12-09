package com.example.fitness_tracker.Repository

import com.example.fitness_tracker.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success("Sign in Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(
        name: String,
        email: String,
        pass: String,
        age: Int,
        gender: String,
        height: Double,
        weight: Double
    ): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("error in getting id")
            val userProfile = User(
                uid = uid,
                name=name,
                email = email,
                age = age,
                gender = gender,
                height = height,
                weight = weight
            )
            firestore.collection("users").document(uid).set(userProfile).await()
            Result.success("Account Created Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getUser(uid: String): Result<User> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            val profile = snapshot.toObject(User::class.java)
            if (profile != null) {
                Result.success(profile)
            } else {
                Result.failure(Exception("Profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun updateUser(user: User): Result<String> {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            Result.success("Updated Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun logout() {
        auth.signOut()
    }
}