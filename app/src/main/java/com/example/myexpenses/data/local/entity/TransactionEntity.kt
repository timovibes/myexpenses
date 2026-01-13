package com.example.myexpenses.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myexpenses.domain.model.*

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val amount: Double,
    val category: String,
    val type: String,
    val description: String,
    val date: Long,
    val receiptUrl: String?,
    val isRecurring: Boolean,
    val recurringPeriod: String,
    val tags: String,
    val syncStatus: String
)

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    userId = userId,
    amount = amount,
    category = Category.valueOf(category),
    type = TransactionType.valueOf(type),
    description = description,
    date = date,
    receiptUrl = receiptUrl,
    isRecurring = isRecurring,
    recurringPeriod = RecurringPeriod.valueOf(recurringPeriod),
    tags = tags.split(",").filter { it.isNotEmpty() },
    syncStatus = SyncStatus.valueOf(syncStatus)
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    userId = userId,
    amount = amount,
    category = category.name,
    type = type.name,
    description = description,
    date = date,
    receiptUrl = receiptUrl,
    isRecurring = isRecurring,
    recurringPeriod = recurringPeriod.name,
    tags = tags.joinToString(","),
    syncStatus = syncStatus.name
)