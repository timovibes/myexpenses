package com.example.myexpenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.myexpenses.presentation.navigation.NavGraph
import com.example.myexpenses.presentation.navigation.Screen
import com.example.myexpenses.presentation.theme.TimsAIExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimsAIExpenseTrackerTheme {
                val systemUiController = rememberSystemUiController()
                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundColor,
                        darkIcons = false
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // FIXED: Correct NavGraph call
                    NavGraph(
                        navController = navController,
                        startDestination = Screen.Onboarding
                    )
                }
            }
        }
    }
}