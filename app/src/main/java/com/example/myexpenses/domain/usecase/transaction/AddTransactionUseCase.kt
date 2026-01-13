package com.example.myexpenses.domain.usecase.transaction

import com.example.myexpenses.domain.model.Transaction
import com.example.myexpenses.domain.repository.TransactionRepository
import com.example.myexpenses.util.NetworkResult
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): NetworkResult<Unit> {
        if (transaction.amount <= 0) {
            return NetworkResult.Error("Amount must be greater than 0")
        }
        if (transaction.description.isBlank()) {
            return NetworkResult.Error("Description cannot be empty")
        }
        return repository.insertTransaction(transaction)
    }
}