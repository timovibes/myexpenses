package com.example.myexpenses.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.myexpenses.domain.model.AppSettings
import com.example.myexpenses.domain.model.BackupFrequency
import com.example.myexpenses.domain.model.DarkModePreference
import com.example.myexpenses.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val CURRENCY = stringPreferencesKey("currency")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val BUDGET_ALERTS = booleanPreferencesKey("budget_alerts")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val AUTO_BACKUP = booleanPreferencesKey("auto_backup")
        val BACKUP_FREQUENCY = stringPreferencesKey("backup_frequency")
        val SYNC_ENABLED = booleanPreferencesKey("sync_enabled")
        val RECEIPT_AUTO_SCAN = booleanPreferencesKey("receipt_auto_scan")
        val VOICE_INPUT_ENABLED = booleanPreferencesKey("voice_input_enabled")
    }

    override fun getSettings(userId: String): Flow<AppSettings> {
        return context.dataStore.data.map { preferences ->
            AppSettings(
                userId = preferences[PreferencesKeys.USER_ID] ?: userId,
                currency = preferences[PreferencesKeys.CURRENCY] ?: "USD",
                language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                darkMode = DarkModePreference.valueOf(
                    preferences[PreferencesKeys.DARK_MODE] ?: "SYSTEM"
                ),
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                budgetAlerts = preferences[PreferencesKeys.BUDGET_ALERTS] ?: true,
                biometricEnabled = preferences[PreferencesKeys.BIOMETRIC_ENABLED] ?: false,
                autoBackup = preferences[PreferencesKeys.AUTO_BACKUP] ?: true,
                backupFrequency = BackupFrequency.valueOf(
                    preferences[PreferencesKeys.BACKUP_FREQUENCY] ?: "DAILY"
                ),
                syncEnabled = preferences[PreferencesKeys.SYNC_ENABLED] ?: true,
                receiptAutoScan = preferences[PreferencesKeys.RECEIPT_AUTO_SCAN] ?: true,
                voiceInputEnabled = preferences[PreferencesKeys.VOICE_INPUT_ENABLED] ?: true
            )
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = settings.userId
            preferences[PreferencesKeys.CURRENCY] = settings.currency
            preferences[PreferencesKeys.LANGUAGE] = settings.language
            preferences[PreferencesKeys.DARK_MODE] = settings.darkMode.name
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            preferences[PreferencesKeys.BUDGET_ALERTS] = settings.budgetAlerts
            preferences[PreferencesKeys.BIOMETRIC_ENABLED] = settings.biometricEnabled
            preferences[PreferencesKeys.AUTO_BACKUP] = settings.autoBackup
            preferences[PreferencesKeys.BACKUP_FREQUENCY] = settings.backupFrequency.name
            preferences[PreferencesKeys.SYNC_ENABLED] = settings.syncEnabled
            preferences[PreferencesKeys.RECEIPT_AUTO_SCAN] = settings.receiptAutoScan
            preferences[PreferencesKeys.VOICE_INPUT_ENABLED] = settings.voiceInputEnabled
        }
    }

    override suspend fun clearSettings(userId: String) {
        context.dataStore.edit { it.clear() }
    }
}