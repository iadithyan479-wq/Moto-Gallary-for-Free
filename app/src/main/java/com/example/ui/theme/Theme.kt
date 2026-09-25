package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PitchBlack,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = PureWhite,
    secondary = OffWhite,
    onSecondary = PitchBlack,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = OffWhite,
    tertiary = GrayMuted,
    onTertiary = PitchBlack,
    background = PitchBlack,
    onBackground = PureWhite,
    surface = PitchBlack,
    onSurface = PureWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = GrayMuted,
    outline = DarkBorderHighContrast,
    outlineVariant = DarkBorder,
    error = VaultRed,
    onError = PitchBlack
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = PitchBlack,
    onPrimary = PureWhite,
    primaryContainer = OffWhite,
    onPrimaryContainer = PitchBlack,
    secondary = DarkSurfaceVariant,
    onSecondary = PureWhite,
    secondaryContainer = OffWhite,
    onSecondaryContainer = PitchBlack,
    tertiary = GraySubtle,
    onTertiary = PureWhite,
    background = PureWhite,
    onBackground = PitchBlack,
    surface = PureWhite,
    onSurface = PitchBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = DarkSurface,
    outline = DarkBorderHighContrast,
    outlineVariant = LightBorder,
    error = VaultRed,
    onError = PureWhite
)

@Composable
fun MotoGalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) HighContrastDarkColorScheme else HighContrastDarkColorScheme // User requested minimal black and white dark mode for night viewing / eye strain

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = PitchBlack.toArgb()
                it.navigationBarColor = PitchBlack.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
