package com.example.myexpenses.domain.repository

import com.example.myexpenses.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(userId: String): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun clearSettings(userId: String)
}