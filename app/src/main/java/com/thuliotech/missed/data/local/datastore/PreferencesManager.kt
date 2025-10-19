package com.thuliotech.missed.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de preferências usando DataStore
 * Armazena configurações do app de forma assíncrona e type-safe
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PREFERENCES_NAME
    )

    companion object {
        private const val PREFERENCES_NAME = "missed_preferences"
        
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    /**
     * Verifica se é o primeiro acesso do usuário
     */
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_LAUNCH] ?: true
    }

    /**
     * Verifica se o usuário está logado
     */
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    /**
     * Marca que o onboarding foi completado
     */
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = false
        }
    }

    /**
     * Marca que o usuário fez login
     */
    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    /**
     * Limpa todas as preferências (útil para logout)
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

