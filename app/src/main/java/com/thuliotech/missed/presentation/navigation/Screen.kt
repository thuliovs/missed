package com.thuliotech.missed.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Definição de rotas usando Type-Safe Navigation
 * Todas as telas do app são definidas aqui como sealed classes
 */
sealed interface Screen {
    
    /**
     * Fluxo de Onboarding
     */
    @Serializable
    data object Welcome : Screen
    
    @Serializable
    data object Explanation : Screen
    
    @Serializable
    data object Benefits : Screen
    
    @Serializable
    data object Login : Screen
    
    /**
     * Telas principais do app
     */
    @Serializable
    data object Home : Screen
    
    @Serializable
    data object Map : Screen
    
    @Serializable
    data class AlarmDetail(val alarmId: String) : Screen
    
    @Serializable
    data object Settings : Screen
}

