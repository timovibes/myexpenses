package com.example.myexpenses.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val currency: String = "KSH",
    val monthlyBudget: Double = 0.0
)