package com.example.fitness_tracker.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitness_tracker.viewmodel.auth_viewmodel.AuthViewModel
import com.example.fitness_tracker.viewmodel.water_viewmodel.WaterViewModel
import com.example.fitness_tracker.viewmodel.food_viewmodel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    waterViewModel: WaterViewModel,
    foodViewModel: FoodViewModel,
    onNavigateToWater: () -> Unit,
    onNavigateToFood: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by authViewModel.currentUserProfile.collectAsState()
    val totalWaterIntake by waterViewModel.totalIntake.collectAsState()
    val waterGoal by waterViewModel.dailyWaterGoal.collectAsState()
    val todayCalories by foodViewModel.todayCalories.collectAsState()
    val calorieGoal by foodViewModel.dailyCalorieGoal.collectAsState()

    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.fetchUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello, ${currentUser?.name ?: "User"}! 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
                                .format(Date()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                showMenu = false
                                onNavigateToProfile()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // User Stats Overview
            item {
                UserStatsCard(currentUser)
            }

            // Quick Actions
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionCard(
                            icon = Icons.Default.WaterDrop,
                            title = "Water",
                            color = Color(0xFF2196F3),
                            onClick = onNavigateToWater
                        )
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.Restaurant,
                            title = "Food",
                            color = Color(0xFFFF9800),
                            onClick = onNavigateToFood
                        )
                    }
                    item {
                        QuickActionCard(
                            icon = Icons.Default.FitnessCenter,
                            title = "Workout",
                            color = Color(0xFF4CAF50),
                            onClick = { /* Navigate to workout */ }
                        )
                    }
                }
            }

            // Today's Summary
            item {
                Text(
                    text = "Today's Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Water & Food Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        icon = Icons.Default.WaterDrop,
                        title = "Water",
                        value = "$totalWaterIntake ml",
                        goal = "$waterGoal ml",
                        progress = if (waterGoal > 0) totalWaterIntake.toFloat() / waterGoal.toFloat() else 0f,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToWater
                    )

                    SummaryCard(
                        icon = Icons.Default.Restaurant,
                        title = "Calories",
                        value = "$todayCalories",
                        goal = "$calorieGoal",
                        progress = if (calorieGoal > 0) todayCalories.toFloat() / calorieGoal.toFloat() else 0f,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFood
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun UserStatsCard(user: com.example.fitness_tracker.database.User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Your Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Weight",
                    value = "${user?.weight ?: 0.0} kg",
                    icon = Icons.Default.MonitorWeight
                )
                StatItem(
                    label = "Height",
                    value = "${user?.height ?: 0.0} cm",
                    icon = Icons.Default.Height
                )
                StatItem(
                    label = "Age",
                    value = "${user?.age ?: 0} yrs",
                    icon = Icons.Default.Cake
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun SummaryCard(
    icon: ImageVector,
    title: String,
    value: String,
    goal: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = "of $goal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}