package com.example.myexpenses.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.model.*
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.domain.usecase.analytics.GetFinancialSummaryUseCase
import com.example.myexpenses.domain.usecase.transaction.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val user: User? = null,
    val financialSummary: FinancialSummary = FinancialSummary(),
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface DashboardEvent {
    data object Refresh : DashboardEvent
    data object LoadData : DashboardEvent
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.Refresh, DashboardEvent.LoadData -> loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                authRepository.currentUser.collectLatest { user ->
                    if (user != null) {
                        _state.update { it.copy(user = user) }

                        combine(
                            getTransactionsUseCase(user.id),
                            getFinancialSummaryUseCase(user.id)
                        ) { transactions, summary ->
                            _state.update {
                                it.copy(
                                    recentTransactions = transactions.take(5),
                                    financialSummary = summary,
                                    isLoading = false
                                )
                            }
                        }.collect()
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = e.message ?: "An error occurred")
                }
            }
        }
    }
}