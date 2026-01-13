package com.example.myexpenses.domain.repository

import com.example.myexpenses.domain.model.User
import com.example.myexpenses.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): NetworkResult<User>
    suspend fun signUp(email: String, password: String, displayName: String): NetworkResult<User>
    suspend fun signOut(): NetworkResult<Unit>
    suspend fun resetPassword(email: String): NetworkResult<Unit>
    suspend fun getCurrentUserId(): String?
}