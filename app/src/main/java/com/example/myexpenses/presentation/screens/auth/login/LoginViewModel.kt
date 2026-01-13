package com.example.myexpenses.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpenses.domain.usecase.auth.SignInUseCase
import com.example.myexpenses.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed interface LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent
    data class PasswordChanged(val password: String) : LoginEvent
    data object SignIn : LoginEvent
    data object ClearError : LoginEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, error = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, error = null) }
            }
            is LoginEvent.SignIn -> signIn()
            is LoginEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = signInUseCase(state.value.email, state.value.password)) {
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
}