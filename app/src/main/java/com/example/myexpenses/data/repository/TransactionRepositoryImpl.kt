package com.example.myexpenses.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.example.myexpenses.data.local.dao.TransactionDao
import com.example.myexpenses.data.local.entity.toDomain
import com.example.myexpenses.data.local.entity.toEntity
import com.example.myexpenses.data.remote.mapper.FirebaseMappers.toFirebaseMap
import com.example.myexpenses.data.remote.mapper.FirebaseMappers.toTransaction
import com.example.myexpenses.domain.model.SyncStatus
import com.example.myexpenses.domain.model.Transaction
import com.example.myexpenses.domain.repository.TransactionRepository
import com.example.myexpenses.util.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore
) : TransactionRepository {

    override fun getAllTransactions(userId: String): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(userId, startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    override suspend fun insertTransaction(transaction: Transaction): NetworkResult<Unit> {
        return try {
            transactionDao.insertTransaction(transaction.copy(syncStatus = SyncStatus.PENDING).toEntity())

            firestore.collection("transactions")
                .document(transaction.id)
                .set(transaction.toFirebaseMap())
                .await()

            transactionDao.updateTransaction(transaction.copy(syncStatus = SyncStatus.SYNCED).toEntity())
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            transactionDao.updateTransaction(transaction.copy(syncStatus = SyncStatus.FAILED).toEntity())
            NetworkResult.Error(e.message ?: "Failed to insert transaction")
        }
    }

    override suspend fun updateTransaction(transaction: Transaction): NetworkResult<Unit> {
        return try {
            transactionDao.updateTransaction(transaction.toEntity())

            firestore.collection("transactions")
                .document(transaction.id)
                .update(transaction.toFirebaseMap())
                .await()

            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to update transaction")
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction): NetworkResult<Unit> {
        return try {
            transactionDao.deleteTransaction(transaction.toEntity())
            firestore.collection("transactions").document(transaction.id).delete().await()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to delete transaction")
        }
    }

    override suspend fun syncTransactions(userId: String): NetworkResult<Unit> {
        return try {
            val remoteTransactions = firestore.collection("transactions")
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toTransaction() }

            // FIXED: Use insertTransactions (plural) not insertTransaction
            transactionDao.insertTransactions(remoteTransactions.map { it.toEntity() })
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Sync failed")
        }
    }
}