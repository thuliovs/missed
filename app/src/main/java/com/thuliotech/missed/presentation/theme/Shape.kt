package com.thuliotech.missed.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Sistema de shapes seguindo Material Design 3
 */
val MissedShapes = Shapes(
    // Extra Small - Chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - Buttons, text fields
    small = RoundedCornerShape(8.dp),
    
    // Medium - Cards, dialogs
    medium = RoundedCornerShape(12.dp),
    
    // Large - Bottom sheets, large cards
    large = RoundedCornerShape(16.dp),
    
    // Extra Large - Full screen dialogs
    extraLarge = RoundedCornerShape(28.dp)
)

