package com.thuliotech.missed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.thuliotech.missed.presentation.navigation.NavGraph
import com.thuliotech.missed.presentation.theme.MissedTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do app
 * Gerencia a navegação e o tema
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MissedTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}