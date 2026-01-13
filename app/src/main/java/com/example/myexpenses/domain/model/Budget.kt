package com.example.myexpenses.domain.model

import java.util.UUID

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val category: Category = Category.OTHER,
    val amount: Double = 0.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDate: Long = System.currentTimeMillis(),
    val alertThreshold: Double = 0.8
)

enum class BudgetPeriod {
    WEEKLY, MONTHLY, YEARLY
}