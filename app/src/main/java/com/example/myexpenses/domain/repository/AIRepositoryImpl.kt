package com.example.myexpenses.data.repository

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.example.myexpenses.domain.repository.AIRepository
import com.example.myexpenses.util.NetworkResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AIRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel
) : AIRepository {

    override suspend fun generateFinancialInsights(prompt: String): NetworkResult<String> {
        return try {
            val response = generativeModel.generateContent(
                content {
                    // Use a supported model
                    text("""
                    You are a personal finance advisor. Analyze the following financial data and provide 
                    actionable insights, spending patterns, and recommendations.
                    
                    $prompt
                    
                    Provide a clear, concise analysis with specific recommendations.
                """.trimIndent())
                }
            )
            val insights = response.text?.takeIf { it.isNotBlank() } ?: "No insights generated"
            NetworkResult.Success(insights)
        } catch (e: Exception) {
            // Catch ServerException separately for better debugging
            NetworkResult.Error(e.message ?: "Failed to generate insights")
        }
    }



    override suspend fun analyzeReceipt(bitmap: Bitmap): NetworkResult<String> {
        return try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()

            val extractedText = result.text
            if (extractedText.isEmpty()) {
                return NetworkResult.Error("No text found in receipt")
            }

            val aiResponse = generativeModel.generateContent(
                content {
                    text("""
                        Extract the following information from this receipt text:
                        - Total amount
                        - Store/merchant name
                        - Date
                        - Items purchased (if visible)
                        
                        Receipt text:
                        $extractedText
                        
                        Return the information in this exact format:
                        Amount: [amount]
                        Merchant: [merchant name]
                        Date: [date]
                        Items: [items list]
                    """.trimIndent())
                }
            )

            NetworkResult.Success(aiResponse.text ?: extractedText)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Receipt analysis failed")
        }
    }

    override suspend fun processVoiceInput(text: String): NetworkResult<Map<String, String>> {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text("""
                        Parse this natural language expense description into structured data:
                        "$text"
                        
                        Extract and return ONLY in this format (no extra text):
                        amount: [numeric amount only]
                        category: [one of: FOOD, TRANSPORT, SHOPPING, ENTERTAINMENT, BILLS, HEALTH, EDUCATION, TRAVEL, SALARY, INVESTMENT, OTHER]
                        description: [brief description]
                        
                        If you can't determine something, use: amount: 0, category: OTHER, description: [original text]
                    """.trimIndent())
                }
            )

            val result = response.text ?: return NetworkResult.Error("Failed to process voice input")

            val map = mutableMapOf<String, String>()
            result.lines().forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    map[parts[0].trim().lowercase()] = parts[1].trim()
                }
            }

            NetworkResult.Success(map)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Voice processing failed")
        }
    }
}