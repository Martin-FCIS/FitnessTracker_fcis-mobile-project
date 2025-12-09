package com.example.fitness_tracker.ui.water

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitness_tracker.data.water.WaterEntity
import com.example.fitness_tracker.viewmodel.water_viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    waterViewModel: WaterViewModel,
    onNavigateBack: () -> Unit
) {
    val totalWaterIntake by waterViewModel.totalIntake.collectAsState()
    val waterGoal by waterViewModel.dailyWaterGoal.collectAsState()
    val waterRecords by waterViewModel.records.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("") }

    val progress = if (waterGoal > 0) (totalWaterIntake.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f) else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Water Tracker") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF2196F3)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Water",
                    tint = Color.White
                )
            }
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

            // Water Goal Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Water Drop Icon
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "$totalWaterIntake ml",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )

                        Text(
                            text = "of $waterGoal ml daily goal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = Color(0xFF2196F3),
                            trackColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val remaining = (waterGoal - totalWaterIntake).coerceAtLeast(0)
                        Text(
                            text = if (remaining > 0) "$remaining ml remaining" else "Goal achieved! 🎉",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (remaining > 0) MaterialTheme.colorScheme.onSurfaceVariant
                            else Color(0xFF4CAF50),
                            fontWeight = if (remaining > 0) FontWeight.Normal else FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Add Buttons
            item {
                Text(
                    text = "Quick Add",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickAddButton(
                        amount = 100,
                        onClick = { waterViewModel.addWater(100) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAddButton(
                        amount = 250,
                        onClick = { waterViewModel.addWater(250) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAddButton(
                        amount = 500,
                        onClick = { waterViewModel.addWater(500) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Today's Records
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Records",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${waterRecords.filter {
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.HOUR_OF_DAY, 0)
                            calendar.set(Calendar.MINUTE, 0)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)
                            it.timestamp >= calendar.timeInMillis
                        }.size} entries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(
                items = waterRecords.filter { record ->
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    record.timestamp >= calendar.timeInMillis
                }
            ) { record ->
                WaterRecordCard(
                    record = record,
                    onDelete = { waterViewModel.deleteWater(record) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add Water Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add Water",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text("Quick amounts:")
                    Spacer(modifier = Modifier.height(12.dp))

                    listOf(100, 250, 500, 750, 1000).forEach { amount ->
                        OutlinedButton(
                            onClick = {
                                waterViewModel.addWater(amount)
                                showAddDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("$amount ml")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Custom amount:")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customAmount,
                        onValueChange = { customAmount = it },
                        label = { Text("Amount (ml)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customAmount.toIntOrNull()
                        if (amount != null && amount > 0) {
                            waterViewModel.addWater(amount)
                            customAmount = ""
                            showAddDialog = false
                        }
                    },
                    enabled = customAmount.toIntOrNull() != null && customAmount.toInt() > 0
                ) {
                    Text("Add Custom")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    customAmount = ""
                    showAddDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun QuickAddButton(
    amount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "ml",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun WaterRecordCard(
    record: WaterEntity,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2196F3).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${record.amount} ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            .format(Date(record.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Record?") },
            text = { Text("Are you sure you want to delete this water intake record?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}