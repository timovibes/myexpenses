package com.example.myexpenses.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myexpenses.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val createdAt: Long,
    val currency: String,
    val monthlyBudget: Double
)

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    createdAt = createdAt,
    currency = currency,
    monthlyBudget = monthlyBudget
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    createdAt = createdAt,
    currency = currency,
    monthlyBudget = monthlyBudget
)