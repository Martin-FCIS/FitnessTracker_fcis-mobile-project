package com.example.fitness_tracker.Repository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
class AuthRepo {
    private val auth = FirebaseAuth.getInstance()

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

    suspend fun signUp(email: String, pass: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success("Sign up Successfully")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun logout() {
        auth.signOut()
    }
}