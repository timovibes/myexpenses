package com.example.myexpenses.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.model.*
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val user: User? = null,
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true,
    val showLogoutDialog: Boolean = false,
    val showClearDataDialog: Boolean = false,
    val error: String? = null
)

sealed interface SettingsEvent {
    data class UpdateCurrency(val currency: String) : SettingsEvent
    data class UpdateLanguage(val language: String) : SettingsEvent
    data class UpdateDarkMode(val mode: DarkModePreference) : SettingsEvent
    data class UpdateNotifications(val enabled: Boolean) : SettingsEvent
    data class UpdateBudgetAlerts(val enabled: Boolean) : SettingsEvent
    data class UpdateBiometric(val enabled: Boolean) : SettingsEvent
    data class UpdateAutoBackup(val enabled: Boolean) : SettingsEvent
    data class UpdateBackupFrequency(val frequency: BackupFrequency) : SettingsEvent
    data class UpdateSync(val enabled: Boolean) : SettingsEvent
    data class UpdateReceiptScan(val enabled: Boolean) : SettingsEvent
    data class UpdateVoiceInput(val enabled: Boolean) : SettingsEvent
    data object ShowLogoutDialog : SettingsEvent
    data object HideLogoutDialog : SettingsEvent
    data object Logout : SettingsEvent
    data object ShowClearDataDialog : SettingsEvent
    data object HideClearDataDialog : SettingsEvent
    data object ClearData : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateCurrency -> updateSettings { it.copy(currency = event.currency) }
            is SettingsEvent.UpdateLanguage -> updateSettings { it.copy(language = event.language) }
            is SettingsEvent.UpdateDarkMode -> updateSettings { it.copy(darkMode = event.mode) }
            is SettingsEvent.UpdateNotifications -> updateSettings { it.copy(notificationsEnabled = event.enabled) }
            is SettingsEvent.UpdateBudgetAlerts -> updateSettings { it.copy(budgetAlerts = event.enabled) }
            is SettingsEvent.UpdateBiometric -> updateSettings { it.copy(biometricEnabled = event.enabled) }
            is SettingsEvent.UpdateAutoBackup -> updateSettings { it.copy(autoBackup = event.enabled) }
            is SettingsEvent.UpdateBackupFrequency -> updateSettings { it.copy(backupFrequency = event.frequency) }
            is SettingsEvent.UpdateSync -> updateSettings { it.copy(syncEnabled = event.enabled) }
            is SettingsEvent.UpdateReceiptScan -> updateSettings { it.copy(receiptAutoScan = event.enabled) }
            is SettingsEvent.UpdateVoiceInput -> updateSettings { it.copy(voiceInputEnabled = event.enabled) }
            is SettingsEvent.ShowLogoutDialog -> _state.update { it.copy(showLogoutDialog = true) }
            is SettingsEvent.HideLogoutDialog -> _state.update { it.copy(showLogoutDialog = false) }
            is SettingsEvent.Logout -> logout()
            is SettingsEvent.ShowClearDataDialog -> _state.update { it.copy(showClearDataDialog = true) }
            is SettingsEvent.HideClearDataDialog -> _state.update { it.copy(showClearDataDialog = false) }
            is SettingsEvent.ClearData -> clearData()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                combine(
                    authRepository.currentUser,
                    authRepository.currentUser.flatMapLatest { user ->
                        if (user != null) {
                            settingsRepository.getSettings(user.id)
                        } else {
                            flowOf(AppSettings())
                        }
                    }
                ) { user, settings ->
                    _state.update {
                        it.copy(
                            user = user,
                            settings = settings,
                            isLoading = false
                        )
                    }
                }.collect()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load settings")
                }
            }
        }
    }

    private fun updateSettings(update: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            val newSettings = update(state.value.settings)
            settingsRepository.updateSettings(newSettings)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _state.update { it.copy(showLogoutDialog = false) }
        }
    }

    private fun clearData() {
        viewModelScope.launch {
            val userId = state.value.user?.id ?: return@launch
            settingsRepository.clearSettings(userId)
            _state.update { it.copy(showClearDataDialog = false) }
        }
    }
}