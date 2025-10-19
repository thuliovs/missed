package com.thuliotech.missed.domain.repository

interface AuthRepository {
    /**
     * Faz login no Firebase usando um idToken do Google
     */
    suspend fun signInWithGoogle(idToken: String)

    /**
     * Faz logout
     */
    suspend fun signOut()

    /**
     * Retorna se há um usuário autenticado
     */
    fun isLoggedIn(): Boolean
}

