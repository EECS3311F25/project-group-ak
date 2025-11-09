package org.example.project.model

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Light theme color scheme
val LightColorScheme = lightColorScheme(
    primary = PRIMARY,
    onPrimary = BACKGROUND,
    primaryContainer = PRIMARY.copy(alpha = 0.08f),
    onPrimaryContainer = PRIMARY,
    
    secondary = SECONDARY,
    onSecondary = PRIMARY,
    secondaryContainer = SECONDARY.copy(alpha = 0.12f),
    onSecondaryContainer = PRIMARY,
    
    tertiary = TERTIARY,
    onTertiary = PRIMARY,
    tertiaryContainer = TERTIARY.copy(alpha = 0.12f),
    onTertiaryContainer = PRIMARY,
    
    background = BACKGROUND,
    onBackground = PRIMARY,
    
    surface = BACKGROUND,
    onSurface = PRIMARY,
    surfaceVariant = BACKGROUND.copy(alpha = 0.7f),
    onSurfaceVariant = PRIMARY.copy(alpha = 0.7f),
    
    error = Color(0xFFB3261E),  // Material default error color
    onError = BACKGROUND,
    errorContainer = Color(0xFFFFDAD6),  // Light red background
    onErrorContainer = Color(0xFF410E0B)  // Dark red text
)