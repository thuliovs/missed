package com.thuliotech.missed.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thuliotech.missed.data.local.datastore.PreferencesManager
import com.thuliotech.missed.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val isSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun signInWithGoogleIdToken(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                authRepository.signInWithGoogle(idToken)
                preferencesManager.setLoggedIn(true)
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = t.message)
            }
        }
    }
}

