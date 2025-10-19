package com.thuliotech.missed.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thuliotech.missed.presentation.theme.MissedTheme
import com.thuliotech.missed.presentation.theme.dimensions
import com.thuliotech.missed.presentation.theme.spacing

/**
 * Botão primário do app Missed
 * Segue o Design System com altura e espaçamento consistentes
 */
@Composable
fun MissedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(MaterialTheme.dimensions.buttonHeight),
        enabled = enabled,
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.spacing.large,
            vertical = MaterialTheme.spacing.medium
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Botão secundário (outlined)
 */
@Composable
fun MissedOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(MaterialTheme.dimensions.buttonHeight),
        enabled = enabled,
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.spacing.large,
            vertical = MaterialTheme.spacing.medium
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Botão de texto (text button)
 */
@Composable
fun MissedTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MissedButtonPreview() {
    MissedTheme {
        MissedButton(
            text = "Continuar",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MissedOutlinedButtonPreview() {
    MissedTheme {
        MissedOutlinedButton(
            text = "Voltar",
            onClick = {}
        )
    }
}

