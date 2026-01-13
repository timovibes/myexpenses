package com.example.myexpenses.data.remote.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.example.myexpenses.domain.model.*

object FirebaseMappers {

    fun DocumentSnapshot.toTransaction(): Transaction? {
        return try {
            Transaction(
                id = id,
                userId = getString("userId") ?: return null,
                amount = getDouble("amount") ?: 0.0,
                category = Category.valueOf(getString("category") ?: "OTHER"),
                type = TransactionType.valueOf(getString("type") ?: "EXPENSE"),
                description = getString("description") ?: "",
                date = getLong("date") ?: System.currentTimeMillis(),
                receiptUrl = getString("receiptUrl"),
                isRecurring = getBoolean("isRecurring") ?: false,
                recurringPeriod = RecurringPeriod.valueOf(getString("recurringPeriod") ?: "NONE"),
                tags = (get("tags") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                syncStatus = SyncStatus.SYNCED
            )
        } catch (e: Exception) {
            null
        }
    }

    fun Transaction.toFirebaseMap(): Map<String, Any?> {
        return hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "category" to category.name,
            "type" to type.name,
            "description" to description,
            "date" to date,
            "receiptUrl" to receiptUrl,
            "isRecurring" to isRecurring,
            "recurringPeriod" to recurringPeriod.name,
            "tags" to tags,
            "lastModified" to System.currentTimeMillis()
        )
    }

    fun DocumentSnapshot.toBudget(): Budget? {
        return try {
            Budget(
                id = id,
                userId = getString("userId") ?: return null,
                category = Category.valueOf(getString("category") ?: "OTHER"),
                amount = getDouble("amount") ?: 0.0,
                period = BudgetPeriod.valueOf(getString("period") ?: "MONTHLY"),
                startDate = getLong("startDate") ?: System.currentTimeMillis(),
                alertThreshold = getDouble("alertThreshold") ?: 0.8
            )
        } catch (e: Exception) {
            null
        }
    }

    fun Budget.toFirebaseMap(): Map<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "category" to category.name,
            "amount" to amount,
            "period" to period.name,
            "startDate" to startDate,
            "alertThreshold" to alertThreshold,
            "lastModified" to System.currentTimeMillis()
        )
    }

    fun DocumentSnapshot.toUser(): User? {
        return try {
            User(
                id = id,
                email = getString("email") ?: return null,
                displayName = getString("displayName") ?: "",
                photoUrl = getString("photoUrl"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                currency = getString("currency") ?: "USD",
                monthlyBudget = getDouble("monthlyBudget") ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    fun User.toFirebaseMap(): Map<String, Any?> {
        return hashMapOf(
            "email" to email,
            "displayName" to displayName,
            "photoUrl" to photoUrl,
            "createdAt" to createdAt,
            "currency" to currency,
            "monthlyBudget" to monthlyBudget,
            "lastModified" to System.currentTimeMillis()
        )
    }
}