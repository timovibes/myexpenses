package com.example.myexpenses.presentation.screens.transaction

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.model.*
import com.example.myexpenses.domain.repository.AIRepository
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.domain.usecase.transaction.AddTransactionUseCase
import com.example.myexpenses.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



data class TransactionState(
    val amount: String = "",
    val description: String = "",
    val category: Category = Category.OTHER,
    val type: TransactionType = TransactionType.EXPENSE,
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod = RecurringPeriod.NONE,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isListeningVoice: Boolean = false,
    val isScanningReceipt: Boolean = false
)

sealed interface TransactionEvent {
    data class AmountChanged(val amount: String) : TransactionEvent
    data class DescriptionChanged(val description: String) : TransactionEvent
    data class CategoryChanged(val category: Category) : TransactionEvent
    data class TypeChanged(val type: TransactionType) : TransactionEvent
    data class RecurringChanged(val isRecurring: Boolean) : TransactionEvent
    data class RecurringPeriodChanged(val period: RecurringPeriod) : TransactionEvent
    data object SaveTransaction : TransactionEvent
    data class ProcessVoiceInput(val text: String) : TransactionEvent
    data class ScanReceipt(val bitmap: Bitmap) : TransactionEvent
    data object ClearError : TransactionEvent
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val authRepository: AuthRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionState())
    val state = _state.asStateFlow()

    fun onEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.AmountChanged -> {
                _state.update { it.copy(amount = event.amount, error = null) }
            }
            is TransactionEvent.DescriptionChanged -> {
                _state.update { it.copy(description = event.description, error = null) }
            }
            is TransactionEvent.CategoryChanged -> {
                _state.update { it.copy(category = event.category) }
            }
            is TransactionEvent.TypeChanged -> {
                _state.update { it.copy(type = event.type) }
            }
            is TransactionEvent.RecurringChanged -> {
                _state.update { it.copy(isRecurring = event.isRecurring) }
            }
            is TransactionEvent.RecurringPeriodChanged -> {
                _state.update { it.copy(recurringPeriod = event.period) }
            }
            is TransactionEvent.SaveTransaction -> saveTransaction()
            is TransactionEvent.ProcessVoiceInput -> processVoiceInput(event.text)
            is TransactionEvent.ScanReceipt -> scanReceipt(event.bitmap)
            is TransactionEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun saveTransaction() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            _state.update { it.copy(isLoading = true, error = null) }

            val amount = state.value.amount.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                _state.update {
                    it.copy(isLoading = false, error = "Please enter a valid amount")
                }
                return@launch
            }

            val transaction = Transaction(
                userId = userId,
                amount = amount,
                description = state.value.description,
                category = state.value.category,
                type = state.value.type,
                isRecurring = state.value.isRecurring,
                recurringPeriod = if (state.value.isRecurring) state.value.recurringPeriod else RecurringPeriod.NONE
            )

            when (val result = addTransactionUseCase(transaction)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun processVoiceInput(text: String) {
        viewModelScope.launch {
            _state.update { it.copy(isListeningVoice = true) }

            when (val result = aiRepository.processVoiceInput(text)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _state.update {
                        it.copy(
                            amount = data["amount"] ?: "",
                            description = data["description"] ?: text,
                            category = Category.values().find {
                                it.name == data["category"]?.uppercase()
                            } ?: Category.OTHER,
                            isListeningVoice = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isListeningVoice = false,
                            error = "Voice processing failed: ${result.message}"
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun scanReceipt(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.update { it.copy(isScanningReceipt = true) }

            when (val result = aiRepository.analyzeReceipt(bitmap)) {
                is NetworkResult.Success -> {
                    val text = result.data
                    val lines = text.lines()

                    val amountLine = lines.find { it.contains("Amount:", ignoreCase = true) }
                    val merchantLine = lines.find { it.contains("Merchant:", ignoreCase = true) }

                    _state.update {
                        it.copy(
                            amount = amountLine?.substringAfter(":")?.trim() ?: "",
                            description = merchantLine?.substringAfter(":")?.trim() ?: "Receipt scan",
                            isScanningReceipt = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isScanningReceipt = false,
                            error = "Receipt scan failed: ${result.message}"
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }
}