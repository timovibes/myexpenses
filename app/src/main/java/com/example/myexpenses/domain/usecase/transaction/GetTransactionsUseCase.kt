package com.example.myexpenses.domain.usecase.transaction

import com.example.myexpenses.domain.model.Transaction
import com.example.myexpenses.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(userId: String): Flow<List<Transaction>> {
        return repository.getAllTransactions(userId)
    }
}