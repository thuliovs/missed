package com.thuliotech.missed.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
 * Tela 3: Benefícios
 * Design minimalista usando apenas Material Design 3
 */
@Composable
fun BenefitsScreen(
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
                text = "Benefícios",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Descrição
            Text(
                text = "Durma tranquilo durante viagens de ônibus, trem ou metrô. " +
                        "Economize bateria com geofencing inteligente.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.huge))

            // Botão
            MissedButton(
                text = "Continuar",
                onClick = onNextClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BenefitsScreenPreview() {
    MissedTheme {
        BenefitsScreen(onNextClick = {})
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BenefitsScreenDarkPreview() {
    MissedTheme {
        BenefitsScreen(onNextClick = {})
    }
}

