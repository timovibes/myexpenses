package com.example.myexpenses.domain.usecase.auth

import com.example.myexpenses.domain.model.User
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.util.NetworkResult
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, displayName: String): NetworkResult<User> {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            return NetworkResult.Error("All fields are required")
        }
        if (password.length < 6) {
            return NetworkResult.Error("Password must be at least 6 characters")
        }
        return authRepository.signUp(email, password, displayName)
    }
}