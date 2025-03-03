package com.kpitb.mustahiq.update.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.kpitb.mustahiq.update.ui.theme.color.ColorPalettes

@Composable
fun ZakatAndUsherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme) {
        darkColorScheme(
            primary = ColorPalettes.DarkColorPalette.primary,
            onPrimary = ColorPalettes.DarkColorPalette.onPrimary,
            primaryContainer = ColorPalettes.DarkColorPalette.primaryVariant,
            onPrimaryContainer = ColorPalettes.DarkColorPalette.onSurface,
            secondary = ColorPalettes.DarkColorPalette.secondary,
            onSecondary = ColorPalettes.DarkColorPalette.onSecondary,
            background = ColorPalettes.DarkColorPalette.background,
            onBackground = ColorPalettes.DarkColorPalette.onBackground,
            surface = ColorPalettes.DarkColorPalette.surface,
            onSurface = ColorPalettes.DarkColorPalette.onSurface,
            error = ColorPalettes.DarkColorPalette.error,
            onError = ColorPalettes.DarkColorPalette.onError
        )
    }
    else {
        lightColorScheme(
            primary = ColorPalettes.LightColorPalette.primary,
            onPrimary = ColorPalettes.LightColorPalette.onPrimary,
            primaryContainer = ColorPalettes.LightColorPalette.primaryVariant,
            onPrimaryContainer = ColorPalettes.LightColorPalette.onSurface,
            secondary = ColorPalettes.LightColorPalette.secondary,
            onSecondary = ColorPalettes.LightColorPalette.onSecondary,
            background = ColorPalettes.LightColorPalette.background,
            onBackground = ColorPalettes.LightColorPalette.onBackground,
            surface = ColorPalettes.LightColorPalette.surface,
            onSurface = ColorPalettes.LightColorPalette.onSurface,
            error = ColorPalettes.LightColorPalette.error,
            onError = ColorPalettes.LightColorPalette.onError
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}