package com.example.myexpenses.presentation.screens.analytics

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myexpenses.domain.model.Category
import com.example.myexpenses.presentation.theme.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    state: AnalyticsState,
    onEvent: (AnalyticsEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    LaunchedEffect(Unit) {
        onEvent(AnalyticsEvent.RefreshData)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepSpace, DarkBackground, DarkSurface)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Analytics & Insights", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCards(
                    summary = state.financialSummary,
                    currencyFormat = currencyFormat
                )

                MonthlyTrendChart(
                    monthlyData = state.financialSummary.monthlyTrend
                )

                CategoryBreakdownCard(
                    categories = state.financialSummary.categoryBreakdown,
                    currencyFormat = currencyFormat
                )

                AIInsightsCard(
                    insights = state.aiInsights,
                    isLoading = state.isLoadingInsights,
                    onGenerateInsights = { onEvent(AnalyticsEvent.GenerateInsights) }
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = NeonCyan
            )
        }
    }
}

@Composable
fun SummaryCards(
    summary: com.example.myexpenses.domain.model.FinancialSummary,
    currencyFormat: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Income",
            value = currencyFormat.format(summary.totalIncome),
            icon = Icons.Default.TrendingUp,
            color = SuccessGreen,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Expenses",
            value = currencyFormat.format(summary.totalExpenses),
            icon = Icons.Default.TrendingDown,
            color = ErrorRed,
            modifier = Modifier.weight(1f)
        )
    }

    SummaryCard(
        title = "Savings Rate",
        value = String.format("%.1f%%", summary.savingsRate),
        icon = Icons.Default.Savings,
        color = InfoBlue,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun MonthlyTrendChart(
    monthlyData: List<com.example.myexpenses.domain.model.MonthlyData>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Monthly Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (monthlyData.isEmpty()) {
                Text(
                    text = "No data available",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    monthlyData.forEach { data ->
                        MonthlyBar(
                            month = data.month,
                            income = data.income,
                            expense = data.expense
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyBar(
    month: String,
    income: Double,
    expense: Double
) {
    val maxValue = maxOf(income, expense)

    Column {
        Text(
            text = month,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Income Bar
            if (income > 0) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .weight((income / maxValue).toFloat())
                        .background(SuccessGreen, RoundedCornerShape(4.dp))
                )
            } else {
                // Optional: Placeholder for zero state to maintain UI alignment
                Spacer(modifier = Modifier.width(0.dp))
            }

            // Expense Bar
            if (expense > 0) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .weight((expense / maxValue).toFloat())
                        .background(ErrorRed, RoundedCornerShape(4.dp))
                )
            } else {
                Spacer(modifier = Modifier.width(0.dp))
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    categories: Map<Category, Double>,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (categories.isEmpty()) {
                Text(
                    text = "No expenses recorded",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                val total = categories.values.sum()

                categories.entries.sortedByDescending { it.value }.forEach { (category, amount) ->
                    CategoryBreakdownItem(
                        category = category,
                        amount = amount,
                        percentage = if (total > 0) (amount / total * 100) else 0.0,
                        currencyFormat = currencyFormat
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(
    category: Category,
    amount: Double,
    percentage: Double,
    currencyFormat: NumberFormat
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            Text(
                text = currencyFormat.format(amount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = (percentage / 100).toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = NeonCyan,
            trackColor = Color.Gray.copy(alpha = 0.2f)
        )

        Text(
            text = String.format("%.1f%%", percentage),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun AIInsightsCard(
    insights: String,
    isLoading: Boolean,
    onGenerateInsights: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI Insights",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (!isLoading) {
                    IconButton(onClick = onGenerateInsights) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = NeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonPurple
                        )
                    }
                }
                insights.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Generate AI-powered insights about your spending habits",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onGenerateInsights,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPurple
                            )
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Insights")
                        }
                    }
                }
                else -> {
                    Text(
                        text = insights,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = MaterialTheme.typography.bodyMedium.fontSize * 1.5
                    )
                }
            }
        }
    }
}