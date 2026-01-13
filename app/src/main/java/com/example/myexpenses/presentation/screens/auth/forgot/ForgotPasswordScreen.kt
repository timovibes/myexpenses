package com.example.myexpenses.presentation.screens.auth.forgot

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myexpenses.presentation.theme.*

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            kotlinx.coroutines.delay(2000)
            onNavigateBack()
        }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your email address and we'll send you a link to reset your password",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NeonCyan,
                    cursorColor = NeonCyan
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = state.error != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ErrorRed.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = state.error ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AnimatedVisibility(
                visible = state.isSuccess,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Password reset email sent! Check your inbox.",
                        modifier = Modifier.padding(16.dp),
                        color = SuccessGreen,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = { onEvent(ForgotPasswordEvent.ResetPassword) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading && state.email.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Send Reset Link",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}