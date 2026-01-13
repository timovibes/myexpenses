package com.example.myexpenses.domain.model

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val savingsRate: Double = 0.0,
    val topCategory: Category? = null,
    val categoryBreakdown: Map<Category, Double> = emptyMap(),
    val monthlyTrend: List<MonthlyData> = emptyList(),
    val period: String = "This Month"
)

data class MonthlyData(
    val month: String = "",
    val income: Double = 0.0,
    val expense: Double = 0.0
)