package com.example.myexpenses.domain.usecase.auth

import com.example.myexpenses.domain.model.User
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.util.NetworkResult
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): NetworkResult<User> {
        if (email.isBlank() || password.isBlank()) {
            return NetworkResult.Error("Email and password cannot be empty")
        }
        return authRepository.signIn(email, password)
    }
}