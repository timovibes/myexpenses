package com.example.myexpenses.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.model.FinancialSummary
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.domain.usecase.ai.GenerateInsightsUseCase
import com.example.myexpenses.domain.usecase.analytics.GetFinancialSummaryUseCase
import com.example.myexpenses.domain.usecase.transaction.GetTransactionsUseCase
import com.example.myexpenses.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AnalyticsState(
    val financialSummary: FinancialSummary = FinancialSummary(),
    val aiInsights: String = "",
    val isLoadingInsights: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface AnalyticsEvent {
    data object RefreshData : AnalyticsEvent
    data object GenerateInsights : AnalyticsEvent
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val generateInsightsUseCase: GenerateInsightsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state = _state.asStateFlow()

    init {
        loadAnalytics()
    }

    fun onEvent(event: AnalyticsEvent) {
        when (event) {
            AnalyticsEvent.RefreshData -> loadAnalytics()
            AnalyticsEvent.GenerateInsights -> generateAIInsights()
        }
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch

                getFinancialSummaryUseCase(userId).collectLatest { summary ->
                    _state.update {
                        it.copy(
                            financialSummary = summary,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load analytics")
                }
            }
        }
    }

    private fun generateAIInsights() {
        viewModelScope.launch {
            // 1. Set loading state
            _state.update { it.copy(isLoadingInsights = true, error = null) }

            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch

                // FIX: Use .first() instead of .collectLatest to get a snapshot and close the stream
                // Otherwise, this block stays open forever, causing the loading hang.
                val transactions = getTransactionsUseCase(userId).first()

                // 2. Execute AI logic
                when (val result = generateInsightsUseCase(transactions, 5000.0)) {
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                aiInsights = result.data,
                                isLoadingInsights = false
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingInsights = false,
                                error = result.message
                            )
                        }
                    }
                    else -> { /* Handle other states */ }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoadingInsights = false,
                        error = "Connection timeout or database mismatch."
                    )
                }
            }
        }
    }
}