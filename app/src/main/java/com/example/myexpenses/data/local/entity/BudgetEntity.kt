package com.example.myexpenses.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myexpenses.domain.model.*

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val category: String,
    val amount: Double,
    val period: String,
    val startDate: Long,
    val alertThreshold: Double
)

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    userId = userId,
    category = Category.valueOf(category),
    amount = amount,
    period = BudgetPeriod.valueOf(period),
    startDate = startDate,
    alertThreshold = alertThreshold
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    userId = userId,
    category = category.name,
    amount = amount,
    period = period.name,
    startDate = startDate,
    alertThreshold = alertThreshold
)