package com.example.myexpenses.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myexpenses.data.local.dao.UserDao
import com.example.myexpenses.data.local.entity.toEntity
import com.example.myexpenses.data.remote.mapper.FirebaseMappers.toFirebaseMap
import com.example.myexpenses.domain.model.User
import com.example.myexpenses.domain.repository.AuthRepository
import com.example.myexpenses.util.NetworkResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userDao: UserDao
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            trySend(firebaseUser?.let {
                User(
                    id = it.uid,
                    email = it.email ?: "",
                    displayName = it.displayName ?: "",
                    photoUrl = it.photoUrl?.toString()
                )
            })
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): NetworkResult<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return NetworkResult.Error("User not found")

            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "",
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = userDoc.getLong("createdAt") ?: System.currentTimeMillis(),
                currency = userDoc.getString("currency") ?: "USD",
                monthlyBudget = userDoc.getDouble("monthlyBudget") ?: 0.0
            )

            userDao.insertUser(user.toEntity())
            NetworkResult.Success(user)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): NetworkResult<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: return NetworkResult.Error("Failed to create user")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            val user = User(
                id = firebaseUser.uid,
                email = email,
                displayName = displayName,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("users").document(firebaseUser.uid)
                .set(user.toFirebaseMap()).await()

            userDao.insertUser(user.toEntity())
            NetworkResult.Success(user)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signOut(): NetworkResult<Unit> {
        return try {
            firebaseAuth.signOut()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun resetPassword(email: String): NetworkResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Password reset failed")
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}