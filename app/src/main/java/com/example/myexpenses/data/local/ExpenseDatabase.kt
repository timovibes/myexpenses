package com.example.myexpenses.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myexpenses.data.local.dao.BudgetDao
import com.example.myexpenses.data.local.dao.TransactionDao
import com.example.myexpenses.data.local.dao.UserDao
import com.example.myexpenses.data.local.entity.BudgetEntity
import com.example.myexpenses.data.local.entity.TransactionEntity
import com.example.myexpenses.data.local.entity.UserEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao
}