package com.thuliotech.missed.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thuliotech.missed.data.local.datastore.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsável por gerenciar a navegação inicial do app
 * Determina se deve mostrar onboarding ou ir direto para Home
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Screen?>(null)
    val startDestination: StateFlow<Screen?> = _startDestination.asStateFlow()

    init {
        determineStartDestination()
    }

    /**
     * Determina a tela inicial baseado nas preferências
     */
    private fun determineStartDestination() {
        viewModelScope.launch {
            val isFirstLaunch = preferencesManager.isFirstLaunch.first()
            
            _startDestination.value = if (isFirstLaunch) {
                Screen.Welcome
            } else {
                Screen.Home
            }
        }
    }

    /**
     * Marca o onboarding como completo
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted()
        }
    }
}

