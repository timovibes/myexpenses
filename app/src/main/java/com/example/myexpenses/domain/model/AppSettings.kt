package com.example.myexpenses.domain.model

data class AppSettings(
    val userId: String = "",
    val currency: String = "KSH",
    val language: String = "en",
    val darkMode: DarkModePreference = DarkModePreference.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val budgetAlerts: Boolean = true,
    val biometricEnabled: Boolean = false,
    val autoBackup: Boolean = true,
    val backupFrequency: BackupFrequency = BackupFrequency.DAILY,
    val syncEnabled: Boolean = true,
    val receiptAutoScan: Boolean = true,
    val voiceInputEnabled: Boolean = true
)

enum class DarkModePreference {
    LIGHT, DARK, SYSTEM
}

enum class BackupFrequency {
    DAILY, WEEKLY, MONTHLY
}