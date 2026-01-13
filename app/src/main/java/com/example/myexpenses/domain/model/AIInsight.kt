package com.example.myexpenses.domain.model

import java.util.UUID

data class AIInsight(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val type: InsightType = InsightType.GENERAL,
    val priority: InsightPriority = InsightPriority.LOW,
    val timestamp: Long = System.currentTimeMillis(),
    val actionable: Boolean = false,
    val actionText: String? = null
)

enum class InsightType {
    SPENDING_ALERT, SAVING_TIP, BUDGET_WARNING, TREND_ANALYSIS, GENERAL
}

enum class InsightPriority {
    LOW, MEDIUM, HIGH
}