package com.thuliotech.missed.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thuliotech.missed.presentation.home.HomeScreen
import com.thuliotech.missed.presentation.onboarding.BenefitsScreen
import com.thuliotech.missed.presentation.onboarding.ExplanationScreen
import com.thuliotech.missed.presentation.onboarding.LoginScreen
import com.thuliotech.missed.presentation.onboarding.WelcomeScreen

/**
 * Grafo de navegação principal do app
 * Gerencia todas as rotas e transições entre telas
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val startDestination by viewModel.startDestination.collectAsState()

    // Aguarda até determinar a rota inicial
    startDestination?.let { destination ->
        NavHost(
            navController = navController,
            startDestination = destination
        ) {
            // Onboarding Flow
            composable<Screen.Welcome> {
                WelcomeScreen(
                    onNextClick = {
                        navController.navigate(Screen.Explanation)
                    }
                )
            }

            composable<Screen.Explanation> {
                ExplanationScreen(
                    onNextClick = {
                        navController.navigate(Screen.Benefits)
                    }
                )
            }

            composable<Screen.Benefits> {
                BenefitsScreen(
                    onNextClick = {
                        navController.navigate(Screen.Login)
                    }
                )
            }

            composable<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    },
                    onSkipClick = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    }
                )
            }

            // Main App
            composable<Screen.Home> {
                HomeScreen()
            }
        }
    }
}

