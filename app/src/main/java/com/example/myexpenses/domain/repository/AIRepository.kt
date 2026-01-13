package com.example.myexpenses.domain.repository

import android.graphics.Bitmap
import com.example.myexpenses.util.NetworkResult

interface AIRepository {
    suspend fun generateFinancialInsights(prompt: String): NetworkResult<String>
    suspend fun analyzeReceipt(bitmap: Bitmap): NetworkResult<String>
    suspend fun processVoiceInput(text: String): NetworkResult<Map<String, String>>
}