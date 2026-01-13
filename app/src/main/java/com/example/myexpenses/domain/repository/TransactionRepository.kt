package com.example.myexpenses.domain.repository

import com.example.myexpenses.domain.model.Transaction
import com.example.myexpenses.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(userId: String): Flow<List<Transaction>>
    fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun insertTransaction(transaction: Transaction): NetworkResult<Unit>
    suspend fun updateTransaction(transaction: Transaction): NetworkResult<Unit>
    suspend fun deleteTransaction(transaction: Transaction): NetworkResult<Unit>
    suspend fun syncTransactions(userId: String): NetworkResult<Unit>
}