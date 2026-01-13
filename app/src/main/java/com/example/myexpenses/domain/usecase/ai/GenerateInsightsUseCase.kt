package com.example.myexpenses.domain.usecase.ai

import com.example.myexpenses.domain.model.Transaction
import com.example.myexpenses.domain.repository.AIRepository
import com.example.myexpenses.util.NetworkResult
import javax.inject.Inject

class GenerateInsightsUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(transactions: List<Transaction>, monthlyBudget: Double): NetworkResult<String> {
        val totalExpenses = transactions.sumOf { it.amount }
        val categoryBreakdown = transactions.groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.amount } }

        val prompt = """
            Monthly Budget: $$monthlyBudget
            Total Spent: $$totalExpenses
            Budget Remaining: $${monthlyBudget - totalExpenses}
            
            Spending by Category:
            ${categoryBreakdown.entries.joinToString("\n") { "${it.key.displayName}: $${it.value}" }}
            
            Recent Transactions: ${transactions.take(10).joinToString("\n") {
            "${it.description}: $${it.amount} (${it.category.displayName})"
        }}
        """.trimIndent()

        return aiRepository.generateFinancialInsights(prompt)
    }
}