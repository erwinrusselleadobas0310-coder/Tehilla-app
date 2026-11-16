package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val TehillaDarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = NavyBackground,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealBright,
    secondary = TealBright,
    onSecondary = NavyBackground,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = ErrorCoral
)

private val TehillaShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun TehillaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TehillaDarkColorScheme,
        typography = TehillaTypography,
        shapes = TehillaShapes,
        content = content
    )
}
