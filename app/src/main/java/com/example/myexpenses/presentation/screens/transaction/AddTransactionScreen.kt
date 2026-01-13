package com.example.myexpenses.presentation.screens.transaction


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardOptions // ✅ this is correct
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myexpenses.presentation.theme.*
import com.example.myexpenses.domain.model.TransactionType
import com.example.myexpenses.domain.model.Category
import com.example.myexpenses.domain.model.RecurringPeriod
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddTransactionScreen(
    state: TransactionState,
    onEvent: (TransactionEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let { onEvent(TransactionEvent.ScanReceipt(it)) }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            delay(500)
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepSpace, DarkBackground, DarkSurface)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = { Text("Add Transaction", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Transaction Type Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.type == TransactionType.EXPENSE,
                        onClick = { onEvent(TransactionEvent.TypeChanged(TransactionType.EXPENSE)) },
                        label = { Text("Expense") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ErrorRed.copy(alpha = 0.3f),
                            selectedLabelColor = ErrorRed
                        )
                    )
                    FilterChip(
                        selected = state.type == TransactionType.INCOME,
                        onClick = { onEvent(TransactionEvent.TypeChanged(TransactionType.INCOME)) },
                        label = { Text("Income") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessGreen.copy(alpha = 0.3f),
                            selectedLabelColor = SuccessGreen
                        )
                    )
                }

                // Amount TextField
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { onEvent(TransactionEvent.AmountChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount") },
                    leadingIcon = { Text("$", style = MaterialTheme.typography.titleLarge) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        cursorColor = NeonCyan
                    )
                )

                // Description TextField
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onEvent(TransactionEvent.DescriptionChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        cursorColor = NeonCyan
                    )
                )

                // Category Chips
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Category.values()) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = state.category == category,
                            onClick = { onEvent(TransactionEvent.CategoryChanged(category)) }
                        )
                    }
                }

                // Recurring Transaction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recurring Transaction",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Switch(
                        checked = state.isRecurring,
                        onCheckedChange = { onEvent(TransactionEvent.RecurringChanged(it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonCyan,
                            checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
                        )
                    )
                }

                AnimatedVisibility(
                    visible = state.isRecurring,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            listOf(
                                RecurringPeriod.DAILY,
                                RecurringPeriod.WEEKLY,
                                RecurringPeriod.MONTHLY,
                                RecurringPeriod.YEARLY
                            )
                        ) { period ->
                            FilterChip(
                                selected = state.recurringPeriod == period,
                                onClick = { onEvent(TransactionEvent.RecurringPeriodChanged(period)) },
                                label = { Text(period.name) }
                            )
                        }
                    }
                }

                // Quick Actions
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Voice Input Card
                    Card(
                        onClick = {
                            if (micPermission.status.isGranted) {
                                // Handle voice input
                            } else {
                                micPermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = NeonPurple.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = NeonPurple,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Voice", color = Color.White)
                        }
                    }

                    // Scan Receipt Card
                    Card(
                        onClick = {
                            if (cameraPermission.status.isGranted) {
                                cameraLauncher.launch(null)
                            } else {
                                cameraPermission.launchPermissionRequest()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = NeonPink.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Scan",
                                tint = NeonPink,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Scan", color = Color.White)
                        }
                    }
                }

                // Error Card
                AnimatedVisibility(
                    visible = state.error != null,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f)),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = state.error ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = ErrorRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = { onEvent(TransactionEvent.SaveTransaction) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !state.isLoading,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Transaction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) NeonCyan else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NeonCyan.copy(alpha = 0.2f) else DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.displayName.split(" ").first(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}
