package com.thuliotech.missed.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.thuliotech.missed.presentation.components.MissedButton
import com.thuliotech.missed.presentation.theme.MissedTheme
import com.thuliotech.missed.presentation.theme.dimensions
import com.thuliotech.missed.presentation.theme.spacing

/**
 * Tela 2: Explicação do app
 * Design minimalista usando apenas Material Design 3
 */
@Composable
fun ExplanationScreen(
    onNextClick: () -> Unit
) {
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
                text = "Como funciona?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Descrição
            Text(
                text = "Defina um local no mapa e um raio de distância. " +
                        "Quando você entrar nessa área, receberá um alarme para acordar.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.huge))

            // Botão
            MissedButton(
                text = "Próximo",
                onClick = onNextClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExplanationScreenPreview() {
    MissedTheme {
        ExplanationScreen(onNextClick = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExplanationScreenDarkPreview() {
    MissedTheme {
        ExplanationScreen(onNextClick = {})
    }
}

