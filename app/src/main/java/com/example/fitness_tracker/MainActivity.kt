package com.example.fitness_tracker
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthState
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthViewModel
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthViewModelFactory
import com.example.fitness_tracker.data.AppDatabase
import com.example.fitness_tracker.Repository.AuthRepo
import com.example.fitness_tracker.data.UserPreferences
import com.example.fitness_tracker.Repository.WaterRepo
import com.example.fitness_tracker.ui.auth.LoginScreen
import com.example.fitness_tracker.ui.auth.RegisterScreen
import com.example.fitness_tracker.ui.theme.Fitness_TrackerTheme
import com.example.fitness_tracker.viewmodel.water_viewmodel.WaterViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getInstance(applicationContext)
        val authRepo = AuthRepo()
        val waterRepo = WaterRepo(database.waterDao())
        val userPreferences = UserPreferences(applicationContext)
        val authFactory = AuthViewModelFactory(authRepo, userPreferences)
        val waterFactory = WaterViewModelFactory(waterRepo, authRepo, userPreferences)
        FirebaseApp.initializeApp(this)
        setContent {
            Fitness_TrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

                    val authState by authViewModel.authState.collectAsState()

                    LaunchedEffect(authState) {
                        when (authState) {
                            is AuthState.Success -> {
                                Toast.makeText(
                                    applicationContext,
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                    popUpTo("register") { inclusive = true }
                                }
                            }

                            is AuthState.Error -> {
                                val errorMsg = (authState as AuthState.Error).message
                                Toast.makeText(
                                    applicationContext,
                                    "error: $errorMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                                print(errorMsg)
                            }

                            else -> {}
                        }
                    }

                    NavHost(navController = navController, startDestination = "login") {

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
                                            name,
                                            email,
                                            pass,
                                            age,
                                            gender,
                                            height,
                                            weight
                                        )
                                    },
                                    onNavigateToLogin = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }

                        composable("home") {
                            HomeScreen(
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

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome in Fitness Tracker! \uD83D\uDCA7",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}