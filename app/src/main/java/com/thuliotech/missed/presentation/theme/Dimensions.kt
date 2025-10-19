package com.thuliotech.missed.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dimensões específicas do app
 * Para tamanhos de componentes, ícones, etc.
 */
data class Dimensions(
    // Tamanhos de ícones
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
    val iconExtraLarge: Dp = 48.dp,
    
    // Tamanhos de botões
    val buttonHeight: Dp = 48.dp,
    val buttonHeightSmall: Dp = 36.dp,
    val buttonHeightLarge: Dp = 56.dp,
    
    // Tamanhos de cards
    val cardElevation: Dp = 2.dp,
    val cardCornerRadius: Dp = 12.dp,
    
    // Tamanhos de imagens
    val avatarSmall: Dp = 32.dp,
    val avatarMedium: Dp = 48.dp,
    val avatarLarge: Dp = 64.dp,
    
    // Tamanhos de campos de texto
    val textFieldHeight: Dp = 56.dp,
    val textFieldCornerRadius: Dp = 8.dp,
    
    // Tamanhos de bottom sheet
    val bottomSheetPeekHeight: Dp = 80.dp,
    
    // Tamanhos de app bar
    val topAppBarHeight: Dp = 64.dp,
    val bottomNavHeight: Dp = 80.dp,
    
    // Dividers
    val dividerThickness: Dp = 1.dp,
    
    // Mapa
    val mapMarkerSize: Dp = 40.dp,
    val mapRadiusStrokeWidth: Dp = 2.dp
)

val LocalDimensions = compositionLocalOf { Dimensions() }

/**
 * Acesso fácil às dimensões
 * Uso: MaterialTheme.dimensions.iconMedium
 */
val MaterialTheme.dimensions: Dimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalDimensions.current

