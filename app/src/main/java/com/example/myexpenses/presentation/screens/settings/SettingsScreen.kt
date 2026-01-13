package com.example.myexpenses.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myexpenses.domain.model.*
import com.example.myexpenses.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
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
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileSection(user = state.user)

                SettingsSection(title = "General") {
                    DropdownSettingItemString(
                        icon = Icons.Default.AttachMoney,
                        title = "Currency",
                        selectedValue = state.settings.currency,
                        options = listOf("USD", "EUR", "GBP", "KES", "JPY", "CAD"),
                        onValueSelected = { onEvent(SettingsEvent.UpdateCurrency(it)) }
                    )

                    DropdownSettingItemPair(
                        icon = Icons.Default.Language,
                        title = "Language",
                        selectedValue = state.settings.language,
                        options = listOf("en" to "English", "es" to "Spanish", "fr" to "French"),
                        onValueSelected = { onEvent(SettingsEvent.UpdateLanguage(it)) }
                    )

                    DropdownSettingItemString(
                        icon = Icons.Default.DarkMode,
                        title = "Theme",
                        selectedValue = state.settings.darkMode.name,
                        options = DarkModePreference.values().map { it.name },
                        onValueSelected = {
                            onEvent(SettingsEvent.UpdateDarkMode(DarkModePreference.valueOf(it)))
                        }
                    )
                }

                SettingsSection(title = "Notifications") {
                    SwitchSettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Receive app notifications",
                        checked = state.settings.notificationsEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateNotifications(it)) }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.Warning,
                        title = "Budget Alerts",
                        subtitle = "Get alerted when approaching budget limits",
                        checked = state.settings.budgetAlerts,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateBudgetAlerts(it)) }
                    )
                }

                SettingsSection(title = "Security") {
                    SwitchSettingItem(
                        icon = Icons.Default.Fingerprint,
                        title = "Biometric Authentication",
                        subtitle = "Use fingerprint or face ID",
                        checked = state.settings.biometricEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateBiometric(it)) }
                    )
                }

                SettingsSection(title = "Data & Sync") {
                    SwitchSettingItem(
                        icon = Icons.Default.Sync,
                        title = "Cloud Sync",
                        subtitle = "Sync data across devices",
                        checked = state.settings.syncEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateSync(it)) }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.Backup,
                        title = "Auto Backup",
                        subtitle = "Automatically backup your data",
                        checked = state.settings.autoBackup,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateAutoBackup(it)) }
                    )

                    if (state.settings.autoBackup) {
                        DropdownSettingItemString(
                            icon = Icons.Default.Schedule,
                            title = "Backup Frequency",
                            selectedValue = state.settings.backupFrequency.name,
                            options = BackupFrequency.values().map { it.name },
                            onValueSelected = {
                                onEvent(SettingsEvent.UpdateBackupFrequency(BackupFrequency.valueOf(it)))
                            }
                        )
                    }
                }

                SettingsSection(title = "Features") {
                    SwitchSettingItem(
                        icon = Icons.Default.CameraAlt,
                        title = "Receipt Auto-Scan",
                        subtitle = "Automatically extract receipt data",
                        checked = state.settings.receiptAutoScan,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateReceiptScan(it)) }
                    )

                    SwitchSettingItem(
                        icon = Icons.Default.Mic,
                        title = "Voice Input",
                        subtitle = "Add expenses with voice commands",
                        checked = state.settings.voiceInputEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.UpdateVoiceInput(it)) }
                    )
                }

                SettingsSection(title = "About") {
                    ClickableSettingItem(
                        icon = Icons.Default.Info,
                        title = "App Version",
                        subtitle = "1.0.0",
                        onClick = { }
                    )

                    ClickableSettingItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = { }
                    )

                    ClickableSettingItem(
                        icon = Icons.Default.Description,
                        title = "Terms of Service",
                        onClick = { }
                    )
                }

                SettingsSection(title = "Danger Zone") {
                    DangerSettingItem(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear All Data",
                        subtitle = "Remove all local data",
                        onClick = { onEvent(SettingsEvent.ShowClearDataDialog) }
                    )

                    DangerSettingItem(
                        icon = Icons.Default.Logout,
                        title = "Sign Out",
                        subtitle = "Sign out of your account",
                        onClick = { onEvent(SettingsEvent.ShowLogoutDialog) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (state.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.HideLogoutDialog) },
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                title = { Text("Sign Out") },
                text = { Text("Are you sure you want to sign out?") },
                confirmButton = {
                    Button(
                        onClick = { onEvent(SettingsEvent.Logout) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed
                        )
                    ) {
                        Text("Sign Out")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(SettingsEvent.HideLogoutDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (state.showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { onEvent(SettingsEvent.HideClearDataDialog) },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed) },
                title = { Text("Clear All Data") },
                text = { Text("This will permanently delete all your local data. This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = { onEvent(SettingsEvent.ClearData) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed
                        )
                    ) {
                        Text("Clear Data")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(SettingsEvent.HideClearDataDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileSection(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.displayName?.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }

            Column {
                Text(
                    text = user?.displayName ?: "User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSurface
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SwitchSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun DropdownSettingItemString(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = selectedValue,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan
                )
            }
        }

        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownSettingItemPair(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selectedValue }?.second ?: selectedValue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = selectedLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonCyan
                )
            }
        }

        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onValueSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ClickableSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun DangerSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = ErrorRed,
            modifier = Modifier.size(24.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = ErrorRed
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ErrorRed.copy(alpha = 0.7f)
                )
            }
        }
    }
}