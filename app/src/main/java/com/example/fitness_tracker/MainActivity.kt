package com.example.fitness_tracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthState
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthViewModel
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthViewModelFactory
import com.example.fitness_tracker.data.AppDatabase
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.Repository.FoodRepo
import com.example.fitness_tracker.Repository.WalkRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.ui.auth.LoginScreen
import com.example.fitness_tracker.ui.auth.RegisterScreen
import com.example.fitness_tracker.ui.food.FoodScreen
import com.example.fitness_tracker.ui.home.HomeScreen
import com.example.fitness_tracker.ui.profile.ProfileScreen
import com.example.fitness_tracker.ui.walk.WalkScreen
import com.example.fitness_tracker.ui.theme.Fitness_TrackerTheme
import com.example.fitness_tracker.ui.water.WaterScreen
import com.example.fitness_tracker.viewmodel.food_viewmodel.FoodViewModel
import com.example.fitness_tracker.viewmodel.food_viewmodel.FoodViewModelFactory
import com.example.fitness_tracker.viewmodel.walk_viewmodel.WalkViewModel
import com.example.fitness_tracker.viewmodel.walk_viewmodel.WalkViewModelFactory
import com.example.fitness_tracker.viewmodel.water_viewmodel.WaterViewModel
import com.example.fitness_tracker.viewmodel.water_viewmodel.WaterViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Setup Database and Repositories
        val database = AppDatabase.getInstance(applicationContext)
        val authRepo = AuthRepo()
        val waterRepo = WaterRepo(database.waterDao())
        val foodRepo = FoodRepo(database.foodDao())
        val walkRepo = WalkRepo(database.walkDao())
        val userPreferences = UserPreferences(applicationContext)

        // Setup ViewModels Factories
        val authFactory = AuthViewModelFactory(authRepo, userPreferences)
        val waterFactory = WaterViewModelFactory(waterRepo, authRepo, userPreferences)
        val foodFactory = FoodViewModelFactory(foodRepo, authRepo, userPreferences)
        val walkFactory = WalkViewModelFactory(walkRepo, authRepo)

        setContent {
            Fitness_TrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // ViewModels
                    val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                    val waterViewModel: WaterViewModel = viewModel(factory = waterFactory)
                    val foodViewModel: FoodViewModel = viewModel(factory = foodFactory)
                    val walkViewModel: WalkViewModel = viewModel(factory = walkFactory)

                    val authState by authViewModel.authState.collectAsState()

                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.Success -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Success!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate to home only if not already there
                                if (navController.currentDestination?.route != "home") {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            }

                            is AuthState.Error -> {
                                val errorMsg = (authState as AuthState.Error).message
                                Toast.makeText(
                                    applicationContext,
                                    "Error: $errorMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            else -> {}
                        }
                    }
                    val startDestination = if (authRepo.getCurrentUserId() != null) "home" else "login"
                    NavHost(navController = navController, startDestination = startDestination) {

                        // Auth Screens
                        composable("login") {
                            if (authState is AuthState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                LoginScreen(
                                    onLoginClick = { email, pass ->
                                        authViewModel.login(email, pass)
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate("register")
                                    }
                                )
                            }
                        }

                        composable("register") {
                            if (authState is AuthState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                RegisterScreen(
                                    onRegisterClick = { name, email, pass, age, gender, height, weight ->
                                        authViewModel.signUp(
                                            name, email, pass, age, gender, height, weight
                                        )
                                    },
                                    onNavigateToLogin = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        // Home Screen
                        composable("home") {
                            HomeScreen(
                                authViewModel = authViewModel,
                                waterViewModel = waterViewModel,
                                foodViewModel = foodViewModel,
                                walkViewModel = walkViewModel,
                                onNavigateToWater = {
                                    navController.navigate("water")
                                },
                                onNavigateToFood = {
                                    navController.navigate("food")
                                },
                                onNavigateToWalk = {
                                    navController.navigate("walk")
                                },
                                onNavigateToProfile = {
                                    navController.navigate("profile")
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Water Screen
                        composable("water") {
                            WaterScreen(
                                waterViewModel = waterViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Food Screen
                        composable("food") {
                            FoodScreen(
                                foodViewModel = foodViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Walk Screen
                        composable("walk") {
                            WalkScreen(
                                walkViewModel = walkViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Profile Screen
                        composable("profile") {
                            ProfileScreen(
                                authViewModel = authViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}