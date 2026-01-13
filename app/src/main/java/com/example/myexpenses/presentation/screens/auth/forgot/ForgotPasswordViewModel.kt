package com.example.myexpenses.presentation.screens.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.myexpenses.util.NetworkResult


data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed interface ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent
    data object ResetPassword : ForgotPasswordEvent
    data object ClearError : ForgotPasswordEvent
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, error = null) }
            }
            is ForgotPasswordEvent.ResetPassword -> resetPassword()
            is ForgotPasswordEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = resetPasswordUseCase(state.value.email)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    // optional: keep loading state
                }
            }
        }
    }
}