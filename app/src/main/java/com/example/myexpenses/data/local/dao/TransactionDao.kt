package com.example.myexpenses.data.local.dao

import androidx.room.*
import com.example.myexpenses.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(userId: String, category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteAllUserTransactions(userId: String)

    @Query("SELECT * FROM transactions WHERE syncStatus = :status")
    suspend fun getTransactionsBySyncStatus(status: String): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = :type AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalByType(userId: String, type: String, startDate: Long, endDate: Long): Double?
}