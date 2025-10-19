package com.thuliotech.missed.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(
        @ApplicationContext context: Context
    ): GoogleSignInOptions {
        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            val webClientId = context.getString(resId)
            builder.requestIdToken(webClientId)
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        gso: GoogleSignInOptions
    ): GoogleSignInClient = GoogleSignIn.getClient(context, gso)
}

