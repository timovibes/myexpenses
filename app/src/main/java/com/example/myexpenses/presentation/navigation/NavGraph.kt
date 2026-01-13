package com.example.myexpenses.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myexpenses.presentation.screens.analytics.AnalyticsScreen
import com.example.myexpenses.presentation.screens.analytics.AnalyticsViewModel
import com.example.myexpenses.presentation.screens.auth.forgot.ForgotPasswordScreen
import com.example.myexpenses.presentation.screens.auth.forgot.ForgotPasswordViewModel
import com.example.myexpenses.presentation.screens.auth.login.LoginScreen
import com.example.myexpenses.presentation.screens.auth.login.LoginViewModel
import com.example.myexpenses.presentation.screens.auth.signup.SignUpScreen
import com.example.myexpenses.presentation.screens.auth.signup.SignUpViewModel
import com.example.myexpenses.presentation.screens.dashboard.DashboardScreen
import com.example.myexpenses.presentation.screens.dashboard.DashboardViewModel
import com.example.myexpenses.presentation.screens.onboarding.OnboardingScreen
import com.example.myexpenses.presentation.screens.settings.SettingsScreen
import com.example.myexpenses.presentation.screens.settings.SettingsViewModel
import com.example.myexpenses.presentation.screens.transaction.AddTransactionScreen
import com.example.myexpenses.presentation.screens.transaction.TransactionViewModel



@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Screen
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            LoginScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            SignUpScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            ForgotPasswordScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            DashboardScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.Analytics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            val viewModel: TransactionViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            AddTransactionScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            AnalyticsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
