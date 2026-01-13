package com.example.myexpenses.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myexpenses.domain.model.*
import com.example.myexpenses.presentation.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepSpace, DarkBackground, DarkSurface)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = state.user?.displayName ?: "User",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                BalanceCard(
                    balance = state.financialSummary.balance,
                    income = state.financialSummary.totalIncome,
                    expenses = state.financialSummary.totalExpenses,
                    currencyFormat = currencyFormat,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                QuickActionsRow(
                    onAddExpense = onNavigateToAddTransaction,
                    onViewAnalytics = onNavigateToAnalytics,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Spending by Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CategoryBreakdownRow(
                    categories = state.financialSummary.categoryBreakdown,
                    currencyFormat = currencyFormat,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    TextButton(onClick = onNavigateToAnalytics) {
                        Text(
                            text = "See All",
                            color = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(state.recentTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    currencyFormat = currencyFormat,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { }
                )
            }

            if (state.recentTransactions.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onNavigateToAddTransaction,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = NeonCyan,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
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
fun BalanceCard(
    balance: Double,
    income: Double,
    expenses: Double,
    currencyFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .rotate(rotation)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                NeonCyan.copy(alpha = 0.1f),
                                NeonPurple.copy(alpha = 0.1f),
                                NeonPink.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currencyFormat.format(balance),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) SuccessGreen else ErrorRed
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Income",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = currencyFormat.format(income),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Expenses",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = currencyFormat.format(expenses),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onAddExpense: () -> Unit,
    onViewAnalytics: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            icon = Icons.Default.Add,
            title = "Add Expense",
            onClick = onAddExpense,
            backgroundColor = NeonCyan,
            modifier = Modifier.weight(1f)
        )

        QuickActionCard(
            icon = Icons.Default.Analytics,
            title = "Analytics",
            onClick = onViewAnalytics,
            backgroundColor = NeonPurple,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = backgroundColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CategoryBreakdownRow(
    categories: Map<Category, Double>,
    currencyFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) {
        return
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(categories.entries.toList()) { (category, amount) ->
            CategoryCard(
                category = category,
                amount = amount,
                currencyFormat = currencyFormat
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    amount: Double,
    currencyFormat: NumberFormat
) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = category.icon,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currencyFormat.format(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currencyFormat: NumberFormat,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.type == TransactionType.INCOME)
                                SuccessGreen.copy(alpha = 0.2f)
                            else
                                ErrorRed.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.category.icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = dateFormat.format(Date(transaction.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${currencyFormat.format(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME) SuccessGreen else ErrorRed
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📊",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No transactions yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Start tracking your expenses",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}