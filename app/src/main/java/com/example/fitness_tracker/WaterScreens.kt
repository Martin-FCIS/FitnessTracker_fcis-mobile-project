package com.example.fitness_tracker

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness_tracker.database.WaterEntity
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun CongratulatoryDialog(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.scale(scale),
        icon = {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.displayLarge
            )
        },
        title = {
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "You've reached your daily water goal!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keep up the great work and stay hydrated! 💧",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Awesome!")
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun SetupScreen(onSave: (Int) -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showAnimation = true
    }

    AnimatedVisibility(
        visible = showAnimation,
        enter = fadeIn() + slideInVertically()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated water drop icon
            val infiniteTransition = rememberInfiniteTransition(label = "bounce")
            val bounce by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bounce"
            )

            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = bounce.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )
            Text(
                text = "HydroTrack",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your personal hydration companion",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = { Text("Your Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show calculated goal preview
            val weight = weightInput.toIntOrNull()
            if (weight != null && weight > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Daily Goal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "${weight * 35} ml",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "≈ ${String.format("%.1f", (weight * 35) / 1000.0)} liters",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val weight = weightInput.toIntOrNull()
                    if (weight != null && weight > 0) {
                        onSave(weight)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = weight != null && weight > 0
            ) {
                Icon(Icons.Default.Start, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Tracking", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TrackerScreen(
    currentIntake: Int,
    dailyGoal: Int,
    history: List<WaterEntity>,
    onAddWater: (Int) -> Unit,
    onDelete: (WaterEntity) -> Unit,
    onUpdateWeight: (Int) -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    val percentage = if (dailyGoal > 0) (currentIntake.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with greeting and settings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getCurrentGreeting(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BMI Calculator Button
                IconButton(
                    onClick = {
                        val intent =
                            context.packageManager.getLaunchIntentForPackage("com.example.bmi_calculator")
                        if (intent != null) {
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "BMI Calculator app not installed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "BMI Calculator",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enhanced circular progress with wave effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            // Outer glow effect
            CircularProgressIndicator(
                progress = { percentage },
                modifier = Modifier.size(220.dp),
                strokeWidth = 16.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                color = getProgressColor(percentage)
            )

            // Inner content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$currentIntake",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = getProgressColor(percentage),
                    fontSize = 48.sp
                )
                Text(
                    text = "/ $dailyGoal ml",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Percentage badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = getProgressColor(percentage).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${(percentage * 100).toInt()}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = getProgressColor(percentage)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Motivational message
        MotivationalMessage(percentage)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Quick Add", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Water amount buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WaterButton(amount = 250, onClick = onAddWater)
            WaterButton(amount = 500, onClick = onAddWater)
            WaterButtonCustom(onClick = onAddWater)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${history.size}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (history.isEmpty()) {
            EmptyHistoryPlaceholder()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history, key = { it.id }) { record ->
                    HistoryItem(record, onDelete)
                }
            }
        }
    }

    if (showEditDialog) {
        EditWeightDialog(
            onDismiss = { showEditDialog = false },
            onConfirm = { newWeight ->
                onUpdateWeight(newWeight)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun MotivationalMessage(percentage: Float) {
    val message = when {
        percentage >= 1.0f -> "Goal achieved! 🎉"
        percentage >= 0.75f -> "Almost there! Keep going! 💪"
        percentage >= 0.5f -> "Halfway done! You're doing great! 👍"
        percentage >= 0.25f -> "Good start! Stay hydrated! 💧"
        else -> "Let's begin your hydration journey! 🚀"
    }

    val color = getProgressColor(percentage)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyHistoryPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No water logged yet",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Text(
            text = "Add your first glass above!",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun EditWeightDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var weightInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Person, contentDescription = null)
        },
        title = { Text("Update Weight") },
        text = {
            Column {
                Text("Enter your new weight to recalculate your daily goal.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                val weight = weightInput.toIntOrNull()
                if (weight != null && weight > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "New goal: ${weight * 35} ml",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val weight = weightInput.toIntOrNull()
                if (weight != null && weight > 0) {
                    onConfirm(weight)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WaterButton(amount: Int, onClick: (Int) -> Unit) {
    var clicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (clicked) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Button(
        onClick = {
            clicked = true
            onClick(amount)
            clicked = false
        },
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
        modifier = Modifier.scale(scale)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("+$amount ml", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WaterButtonCustom(onClick: (Int) -> Unit) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (clicked) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Button(
        onClick = { showCustomDialog = true },
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.scale(scale)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Custom", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showCustomDialog) {
        CustomAmountDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { amount ->
                onClick(amount)
                showCustomDialog = false
            }
        )
    }
}

@Composable
fun CustomAmountDialog(onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var amountInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Edit, contentDescription = null)
        },
        title = { Text("Custom Amount") },
        text = {
            Column {
                Text("Enter any custom water amount")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        Text("Common: 200ml (cup), 330ml (can), 600ml (bottle)")
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountInput.toIntOrNull()
                if (amount != null && amount > 0) {
                    onConfirm(amount)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItem(record: WaterEntity, onDelete: (WaterEntity) -> Unit) {
    Card(
        onClick = { },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "${record.amount} ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTime(record.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            IconButton(onClick = { onDelete(record) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Helper functions
fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun getCurrentGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning! ☀️"
        in 12..16 -> "Good Afternoon! 🌤️"
        in 17..20 -> "Good Evening! 🌅"
        else -> "Good Night! 🌙"
    }
}

fun getProgressColor(percentage: Float): Color {
    return when {
        percentage >= 1.0f -> Color(0xFF4CAF50) // Green - Goal achieved
        percentage >= 0.75f -> Color(0xFF8BC34A) // Light Green
        percentage >= 0.5f -> Color(0xFFFFC107) // Amber
        percentage >= 0.25f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFF2196F3) // Blue - Just started
    }
}