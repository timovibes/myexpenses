package com.example.myexpenses.domain.usecase.auth

import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.util.NetworkResult
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): NetworkResult<Unit> {
        if (email.isBlank()) {
            return NetworkResult.Error("Email cannot be empty")
        }
        return authRepository.resetPassword(email)
    }
}