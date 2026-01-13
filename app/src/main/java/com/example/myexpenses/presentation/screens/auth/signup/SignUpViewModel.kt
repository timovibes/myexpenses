package com.example.myexpenses.presentation.screens.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.myexpenses.util.NetworkResult


data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed interface SignUpEvent {
    data class EmailChanged(val email: String) : SignUpEvent
    data class PasswordChanged(val password: String) : SignUpEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent
    data class DisplayNameChanged(val displayName: String) : SignUpEvent
    data object SignUp : SignUpEvent
    data object ClearError : SignUpEvent
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, error = null) }
            }
            is SignUpEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, error = null) }
            }
            is SignUpEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword, error = null) }
            }
            is SignUpEvent.DisplayNameChanged -> {
                _state.update { it.copy(displayName = event.displayName, error = null) }
            }
            is SignUpEvent.SignUp -> signUp()
            is SignUpEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            if (state.value.password != state.value.confirmPassword) {
                _state.update { it.copy(error = "Passwords do not match") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = signUpUseCase(
                state.value.email,
                state.value.password,
                state.value.displayName
            )) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    // optional
                }
            }

        }
    }
}