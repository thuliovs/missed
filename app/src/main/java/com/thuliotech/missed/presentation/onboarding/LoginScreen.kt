package com.thuliotech.missed.presentation.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.thuliotech.missed.presentation.components.MissedButton
import com.thuliotech.missed.presentation.components.MissedTextButton
import com.thuliotech.missed.presentation.theme.MissedTheme
import com.thuliotech.missed.presentation.theme.spacing
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Tela 4: Login/Autenticação
 * Design minimalista usando apenas Material Design 3
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSkipClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogleIdToken(idToken)
            }
        } catch (e: Exception) {
            // Ignora: usuário cancelou ou erro - manter UI simples
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "Criar conta",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Descrição
            Text(
                text = "Crie uma conta para sincronizar seus alarmes entre dispositivos.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.huge))

            // Botão Login
            MissedButton(
                text = if (uiState.isLoading) "Entrando..." else "Entrar com Google",
                onClick = {
                    val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                        val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                        if (resId != 0) {
                            val webClientId = context.getString(resId)
                            builder.requestIdToken(webClientId)
                        }
                        val gso = builder.build()
                    val client = GoogleSignIn.getClient(context, gso)
                    launcher.launch(client.signInIntent)
                },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // Botão Pular
            MissedTextButton(
                text = "Pular por enquanto",
                onClick = onSkipClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MissedTheme {
        LoginScreen(
            onLoginSuccess = {},
            onSkipClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() {
    MissedTheme {
        LoginScreen(
            onLoginSuccess = {},
            onSkipClick = {}
        )
    }
}

