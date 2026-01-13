package com.example.myexpenses.domain.model

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val amount: Double = 0.0,
    val category: Category = Category.OTHER,
    val type: TransactionType = TransactionType.EXPENSE,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val receiptUrl: String? = null,
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod = RecurringPeriod.NONE,
    val tags: List<String> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(val displayName: String, val icon: String) {
    FOOD("Food & Dining", "🍽️"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    ENTERTAINMENT("Entertainment", "🎬"),
    BILLS("Bills & Utilities", "💡"),
    HEALTH("Healthcare", "🏥"),
    EDUCATION("Education", "📚"),
    TRAVEL("Travel", "✈️"),
    SALARY("Salary", "💰"),
    INVESTMENT("Investment", "📈"),
    OTHER("Other", "📦")
}

enum class RecurringPeriod {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY
}

enum class SyncStatus {
    SYNCED, PENDING, FAILED
}