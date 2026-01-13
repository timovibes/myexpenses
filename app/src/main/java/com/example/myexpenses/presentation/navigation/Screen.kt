package com.example.myexpenses.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object AddTransaction : Screen("add_transaction")
    object Analytics : Screen("analytics")
    object Settings : Screen("settings")
}
