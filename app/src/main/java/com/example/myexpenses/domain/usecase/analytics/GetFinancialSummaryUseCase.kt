package com.example.myexpenses.domain.usecase.analytics

import com.example.myexpenses.domain.model.*
import com.example.myexpenses.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetFinancialSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(userId: String): Flow<FinancialSummary> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.set(currentYear, currentMonth, 1, 0, 0, 0)
        val startDate = calendar.timeInMillis

        calendar.set(currentYear, currentMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        val endDate = calendar.timeInMillis

        return repository.getTransactionsByDateRange(userId, startDate, endDate).map { transactions ->
            val income = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expenses = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            val categoryBreakdown = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key

            val savingsRate = if (income > 0) ((income - expenses) / income) * 100 else 0.0

            FinancialSummary(
                totalIncome = income,
                totalExpenses = expenses,
                balance = income - expenses,
                savingsRate = savingsRate,
                topCategory = topCategory,
                categoryBreakdown = categoryBreakdown,
                monthlyTrend = generateMonthlyTrend(transactions),
                period = "This Month"
            )
        }
    }

    private fun generateMonthlyTrend(transactions: List<Transaction>): List<MonthlyData> {
        val calendar = Calendar.getInstance()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        return (0..5).map { monthsAgo ->
            calendar.add(Calendar.MONTH, -monthsAgo)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            calendar.set(year, month, 1, 0, 0, 0)
            val start = calendar.timeInMillis

            calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val end = calendar.timeInMillis

            val monthTransactions = transactions.filter { it.date in start..end }

            MonthlyData(
                month = monthNames[month],
                income = monthTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount },
                expense = monthTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            )
        }.reversed()
    }
}
